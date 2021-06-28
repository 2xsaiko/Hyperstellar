package net.snakefangox.hyperstellar.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class ShipyardBase extends Block {
	private static final EnumProperty<YardState> YARD_STATE = EnumProperty.of("yard_state", YardState.class);

	public ShipyardBase() {
		super(FabricBlockSettings.of(Material.METAL, MapColor.TERRACOTTA_GREEN)
				.requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F)
				.sounds(BlockSoundGroup.METAL).luminance(value -> value.get(YARD_STATE) == YardState.NORMAL ? 0 : 8));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(YARD_STATE, YardState.NORMAL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(YARD_STATE);
	}

	public BlockState withYardState(YardState state) {
		return getDefaultState().with(YARD_STATE, state);
	}

	public enum YardState implements StringIdentifiable {
		NORMAL("normal"), ACTIVATED("activated"), INVALID("invalid"), EDGE("edge");

		private final String name;

		YardState(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}
	}
}
