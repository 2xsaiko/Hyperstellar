package net.snakefangox.hyperstellar.blocks;

import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.screens.ShipNameplateScreen;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class ShipNameplate extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final Text TITLE = new TranslatableText("hyperstellar.container.ship_nameplate");
	private static final VoxelShape BOX_N = VoxelShapes.cuboid(0.25, 0.25, 1f - 0.0625, 0.75, 0.75, 1);
	private static final VoxelShape BOX_E = VoxelShapes.cuboid(0, 0.25, 0.25, 0.0625, 0.75, 0.75);
	private static final VoxelShape BOX_S = VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.0625);
	private static final VoxelShape BOX_W = VoxelShapes.cuboid(1f - 0.0625, 0.25, 0.25, 1, 0.75, 0.75);

	public ShipNameplate() {
		super(FabricBlockSettings.of(Material.METAL, MapColor.TERRACOTTA_GREEN).requiresTool()
				.breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(ctx.getSide().getHorizontal()));
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
				new ShipNameplateScreen(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		switch (state.get(Properties.HORIZONTAL_FACING)) {
		case EAST:
			return BOX_E;

		case NORTH:
			return BOX_N;

		case SOUTH:
			return BOX_S;

		case WEST:
			return BOX_W;
		}
		return BOX_N;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return HEntities.SHIP_NAMEPLATE.instantiate(pos, state);
	}
}
