package net.snakefangox.hyperstellar.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.hyperstellar.blocks.entities.AbstractGeneratorBE;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class PowerGenerator extends HorizontalFacingBlock implements BlockEntityProvider {

	private final BiFunction<BlockPos, BlockState, AbstractGeneratorBE> beConstructor;

	public PowerGenerator(Settings settings, BiFunction<BlockPos, BlockState, AbstractGeneratorBE> beConstructor) {
		super(settings);
		this.beConstructor = beConstructor;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (world.isClient()) return null;
		return type.supports(state) ? AbstractGeneratorBE::tick : null;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean isPowered = world.isReceivingRedstonePower(pos);
		BlockEntity entityUnchecked = world.getBlockEntity(pos);
		if (entityUnchecked instanceof AbstractGeneratorBE entity) {
			entity.setRedstonePowered(isPowered);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(Properties.POWERED, false);
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

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity entityUnchecked = world.getBlockEntity(pos);
			if (entityUnchecked instanceof Inventory entity) {
				ItemScatterer.spawn(world, pos, entity);
				world.updateComparators(pos,this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity entity = world.getBlockEntity(pos);
		return entity instanceof AbstractGeneratorBE ? (AbstractGeneratorBE) entity : null;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, Properties.POWERED);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return beConstructor.apply(pos, state);
	}
}
