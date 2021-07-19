package net.snakefangox.hyperstellar.blocks;

import net.snakefangox.hyperstellar.ships.ShipData;
import net.snakefangox.hyperstellar.ships.ShipPropertyProvider;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class Thruster extends Block implements ShipPropertyProvider {
	private final double thrust;

	public Thruster(double thrust) {
		super(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F)
				.nonOpaque().sounds(BlockSoundGroup.METAL));
		this.thrust = thrust;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(Properties.FACING, ctx.getSide());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.FACING);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(Properties.FACING)));
	}

	@Override
	public double rawPropChange(String propName, BlockState state, Direction forwards) {
		var axis = state.get(Properties.FACING).getAxis();
		if (axis == Direction.Axis.Y) {
			return propName.equals(ShipData.VERT_THRUST) ? thrust : 0;
		} else if (axis == forwards.getAxis()) {
			return propName.equals(ShipData.FORWARD_THRUST) ? thrust : 0;
		} else {
			return propName.equals(ShipData.SIDE_THRUST) ? thrust : 0;
		}
	}
}
