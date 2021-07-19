package net.snakefangox.hyperstellar.blocks;

import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.worldshell.entity.WorldShellEntity;
import net.snakefangox.worldshell.storage.ShellAwareBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class SeatBlock extends HorizontalFacingBlock implements ShellAwareBlock {
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

	@Override
	public void onUseInShell(World world, WorldShellEntity entity, PlayerEntity player, Hand hand, BlockHitResult hit) {
		var bay = entity.getBay();
		if (bay.isPresent() && entity instanceof ShipEntity) {
			BlockPos seatPos = bay.get().toLocal(hit.getBlockPos());
			((ShipEntity) entity).nextToRide(seatPos);
			player.startRiding(entity);
		}
	}

	public boolean canControl() {
		return canControl;
	}
}
