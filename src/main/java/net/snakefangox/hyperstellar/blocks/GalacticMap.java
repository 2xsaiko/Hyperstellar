package net.snakefangox.hyperstellar.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.screens.GalacticMapScreen;
import org.jetbrains.annotations.Nullable;

public class GalacticMap extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final Text TITLE = new TranslatableText("hyperstellar.container.galactic_map");

	public GalacticMap() {
		super(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY)
				.requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F)
				.sounds(BlockSoundGroup.METAL).luminance(value -> 12));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
				new GalacticMapScreen(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return HEntities.GALACTIC_MAP.instantiate(pos, state);
	}
}
