package net.snakefangox.hyperstellar.blocks.entities;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.hyperstellar.blocks.ShipyardBase;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.worldshell.util.WSNbtHelper;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ShipyardControllerBE extends BlockEntity implements PropertyDelegateHolder {

	public static final int MAX_YARD_SIZE = 100;

	private State state = State.BOOTED;
	private BlockBox yard = new BlockBox(pos);
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

	public ShipyardControllerBE(BlockPos pos, BlockState state) {
		super(HEntities.SHIPYARD_CONTROLLER, pos, state);
	}

	public void getCommand(int command) {
		switch (command) {
			case 0 -> checkShipyardValid();
			case 1 -> scanShip();
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
				if (world.getBlockState(pos).isOf(HBlocks.SHIPYARD_BASE))
					world.setBlockState(pos, HBlocks.SHIPYARD_BASE.withYardState(ShipyardBase.YardState.INVALID));
			}
			state = State.NOTREADY;
		}
	}

	private static boolean isYardSizeValid(BlockBox yardSize) {
		return yardSize.getBlockCountX() > 0 && yardSize.getBlockCountY() > 0 && yardSize.getBlockCountZ() > 0 &&
			   yardSize.getBlockCountX() <= MAX_YARD_SIZE && yardSize.getBlockCountY() <= MAX_YARD_SIZE && yardSize.getBlockCountZ() <= MAX_YARD_SIZE;
	}

	private void scanShip() {
		if (!state.isReady()) return;

	}

	private void tryBuildShip() {
		if (!state.isReady()) return;
		System.out.println("I'm trying");
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
		if (yardNbt instanceof NbtIntArray)
			yard = WSNbtHelper.blockBoxFromNbt(((NbtIntArray)yardNbt).getIntArray());
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
