package net.snakefangox.hyperstellar.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;

public class SeatBlock extends HorizontalFacingBlock {
	private final boolean canControl;

	public SeatBlock(boolean canControl) {
		super(FabricBlockSettings.of(Material.METAL, MapColor.GRAY).requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1).nonOpaque()
				.strength(3.0F, 6.0F).sounds(BlockSoundGroup.WOOL));
		this.canControl = canControl;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public boolean canControl() {
		return canControl;
	}
}
