package net.snakefangox.hyperstellar.blocks.entities;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.snakefangox.hyperstellar.blocks.ShipyardBase;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HClientPackets;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.ships.ShipBuilder;
import net.snakefangox.hyperstellar.ships.ShipData;
import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.worldshell.storage.LocalSpace;
import net.snakefangox.worldshell.transfer.WorldShellConstructor;
import net.snakefangox.worldshell.util.WSNbtHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ShipyardControllerBE extends BlockEntity implements PropertyDelegateHolder {

	public static final int MAX_YARD_SIZE = 100;

	private State state = State.BOOTED;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return state.ordinal();
		}

		@Override
		public void set(int index, int value) {
			state = State.values()[value];
		}

		@Override
		public int size() {
			return 1;
		}
	};
	private BlockBox yard = new BlockBox(pos);

	public ShipyardControllerBE(BlockPos pos, BlockState state) {
		super(HEntities.SHIPYARD_CONTROLLER, pos, state);
	}

	private static boolean isYardSizeValid(BlockBox yardSize) {
		return yardSize.getBlockCountX() > 0 && yardSize.getBlockCountY() > 0 && yardSize.getBlockCountZ() > 0 &&
				yardSize.getBlockCountX() <= MAX_YARD_SIZE && yardSize.getBlockCountY() <= MAX_YARD_SIZE && yardSize.getBlockCountZ() <= MAX_YARD_SIZE;
	}

	public void getCommand(PlayerEntity player, int command) {
		switch (command) {
		case 0 -> checkShipyardValid();
		case 1 -> scanShip(player);
		case 2 -> tryBuildShip();
		}
	}

	private void checkShipyardValid() {
		if (state.isReady()) return;

		Set<BlockPos> checked = new HashSet<>();
		Queue<BlockPos> toCheck = new ArrayDeque<>();
		BlockBox yardSize = new BlockBox(pos);
		BlockPos start = pos.offset(Direction.DOWN);
		toCheck.add(start);
		checked.add(start);

		while (!toCheck.isEmpty()) {
			BlockPos checking = toCheck.poll();
			BlockState state = world.getBlockState(checking);

			if (state.isOf(HBlocks.SHIPYARD_BASE)) {
				yardSize.encompass(checking);
				for (Direction dir : Direction.Type.HORIZONTAL) {
					BlockPos nPos = checking.offset(dir);
					if (!checked.contains(nPos)) {
						toCheck.add(nPos);
						checked.add(nPos);
					}
				}

			} else if (state.isOf(HBlocks.SHIPYARD_TRUSS)) {
				BlockPos nPos = checking.offset(Direction.UP);
				while (world.getBlockState(nPos).isOf(HBlocks.SHIPYARD_TRUSS)) {
					nPos = nPos.offset(Direction.UP);
				}
				yardSize.encompass(nPos.offset(Direction.DOWN));
			}
		}

		BlockBox wholeYard = new BlockBox(yardSize.getMinX(), yardSize.getMinY(), yardSize.getMinZ(),
				yardSize.getMaxX(), yardSize.getMaxY(), yardSize.getMaxZ());
		yardSize.expand(-1);

		if (isYardSizeValid(yardSize) && !yardSize.contains(pos)) {
			Iterable<BlockPos> iter = BlockPos.iterate(wholeYard.getMinX(), wholeYard.getMinY() - 1, wholeYard.getMinZ(),
					wholeYard.getMaxX(), wholeYard.getMinY(), wholeYard.getMaxZ());
			for (BlockPos pos : iter) {
				if (!world.getBlockState(pos).isOf(HBlocks.SHIPYARD_BASE)) continue;
				if (yardSize.contains(pos.offset(Direction.UP))) {
					world.setBlockState(pos, HBlocks.SHIPYARD_BASE.withYardState(ShipyardBase.YardState.ACTIVATED));
				} else {
					world.setBlockState(pos, HBlocks.SHIPYARD_BASE.withYardState(ShipyardBase.YardState.EDGE));
				}
			}

			yard = yardSize;
			state = State.READY;
		} else {
			Iterable<BlockPos> iter = BlockPos.iterate(wholeYard.getMinX(), wholeYard.getMinY(), wholeYard.getMinZ(),
					wholeYard.getMaxX(), wholeYard.getMaxY(), wholeYard.getMaxZ());
			for (BlockPos pos : iter) {
				if (world.getBlockState(pos).isOf(HBlocks.SHIPYARD_BASE)) {
					world.setBlockState(pos, HBlocks.SHIPYARD_BASE.withYardState(ShipyardBase.YardState.INVALID));
				}
			}
			state = State.NOTREADY;
		}
	}

	private void scanShip(PlayerEntity player) {
		if (!state.isReady()) return;

		Direction dir = getCachedState().get(Properties.HORIZONTAL_FACING);
		ShipBuilder shipBuilder = new ShipBuilder(world, LocalSpace.WORLDSPACE, yard, dir);
		shipBuilder.forEachRemaining(blockPos -> {
		});
		ShipData shipData = shipBuilder.getShipData();

		NbtCompound nbt = new NbtCompound();
		nbt.putString("report", shipData.shipReport());

		ServerPlayNetworking.send((ServerPlayerEntity) player, HClientPackets.SH_DATA_PACKET, PacketByteBufs.create().writeNbt(nbt));
	}

	private void tryBuildShip() {
		checkShipyardValid();
		if (!state.isReady()) return;

		Direction dir = getCachedState().get(Properties.HORIZONTAL_FACING);
		ShipBuilder scanShipBuilder = new ShipBuilder(world, LocalSpace.WORLDSPACE, yard, dir);
		scanShipBuilder.forEachRemaining(pos1 -> {
		});

		if (scanShipBuilder.getCount() <= 1) {
			state = State.FAILED;
			return;
		}

		var cog = scanShipBuilder.getCentreOfGravity();
		ShipBuilder shipBuilder = new ShipBuilder(world, LocalSpace.of(cog.getX(), cog.getY(), cog.getZ()), yard, dir);

		WorldShellConstructor.create((ServerWorld) world, HEntities.SHIP, cog, shipBuilder, shipBuilder::loadShipData)
				.construct(this::buildFinished);

		state = State.BUILDING;
	}

	private void buildFinished(WorldShellConstructor.Result<ShipEntity> shipEntityResult) {
		state = State.BUILT;
	}


	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		state = State.values()[nbt.getInt("state")];

		NbtElement yardNbt = nbt.get("yard");
		if (yardNbt instanceof NbtIntArray) {
			yard = WSNbtHelper.blockBoxFromNbt(((NbtIntArray) yardNbt).getIntArray());
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("state", state.ordinal());
		nbt.put("yard", WSNbtHelper.blockBoxToNbt(yard));
		return super.writeNbt(nbt);
	}

	public enum State {
		BOOTED, READY, NOTREADY, BUILDING, BUILT, FAILED;

		public boolean isReady() {
			return this != BOOTED && this != NOTREADY;
		}
	}
}
