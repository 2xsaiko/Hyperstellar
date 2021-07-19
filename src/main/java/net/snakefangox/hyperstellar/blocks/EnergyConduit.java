package net.snakefangox.hyperstellar.blocks;

import net.snakefangox.hyperstellar.blocks.entities.EnergyConduitBE;
import net.snakefangox.hyperstellar.register.HEntities;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class EnergyConduit extends Block implements BlockEntityProvider {

	public static final DirectionProperty PLACED_FACE = Properties.FACING;

	public EnergyConduit() {
		super(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1)
				.strength(1.0F, 3.0F).sounds(BlockSoundGroup.METAL).luminance(value -> 2));
	}

	@Nullable
	private static Direction getRotDir(Direction face, Direction side) {
		if (face.getAxis().isVertical()) {
			return side;
		} else {
			if (face.getAxis() == Direction.Axis.X) {
				return switch (side) {
					case NORTH -> Direction.UP;
					case SOUTH -> Direction.DOWN;
					case EAST -> Direction.NORTH;
					case WEST -> Direction.SOUTH;
					default -> null;
				};
			} else {
				return switch (side) {
					case NORTH -> Direction.UP;
					case SOUTH -> Direction.DOWN;
					case EAST -> Direction.EAST;
					case WEST -> Direction.WEST;
					default -> null;
				};
			}
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction placedFace = ctx.getSide();

		return super.getPlacementState(ctx).with(PLACED_FACE, placedFace);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		Direction placedDir = state.get(PLACED_FACE);
		Direction rDir = getRotDir(placedDir, direction);
		if (rDir == null || !neighborState.isOf(this)) return state;

		BlockEntity entityUnchecked = world.getBlockEntity(pos);
		if (entityUnchecked instanceof EnergyConduitBE entity) {
			entity.setConnect(rDir, getConnectType(world, pos, placedDir, direction));
		}
		return state;
	}

	private ConduitConnection getConnectType(WorldAccess world, BlockPos pos, Direction placedDir, Direction outDir) {
		BlockPos connect = pos.offset(outDir);
		var connectState = world.getBlockState(connect);
		if (connectState.isOf(this)) return ConduitConnection.CONNECT;
		if (world.getBlockState(connect.offset(placedDir)).isOf(this) && connectState.isAir()) {
			return ConduitConnection.SHORT_CORNER;
		}
		if (world.getBlockState(connect.offset(placedDir.getOpposite())).isOf(this)) {
			return ConduitConnection.LONG_CORNER;
		}
		return ConduitConnection.NONE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(PLACED_FACE);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return HEntities.ENERGY_CONDUIT.instantiate(pos, state);
	}

	public enum ConduitConnection implements StringIdentifiable {
		NONE("none"), CONNECT("connect"), SHORT_CORNER("s_corner"), LONG_CORNER("l_corner");

		private final String name;

		ConduitConnection(String name) {
			this.name = name;
		}

		public boolean isConnected() {
			return this != NONE;
		}

		@Override
		public String asString() {
			return name;
		}
	}
}
