package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.snakefangox.hyperstellar.blocks.ShipyardBase;
import net.snakefangox.hyperstellar.blocks.ShipyardController;
import net.snakefangox.hyperstellar.blocks.Thruster;
import net.snakefangox.rapidregister.annotations.BlockMeta;
import net.snakefangox.rapidregister.annotations.RegisterContents;

@RegisterContents(defaultBlockMeta = @BlockMeta(blockItemGroup = "hyperstellar:general"))
public class HBlocks {
	// Resources
	public static final Block TRITANIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	public static final Block TRITANIUM_ORE = new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).strength(3.0F, 6.0F).sounds(BlockSoundGroup.STONE));

	// Shipyard
	public static final ShipyardBase SHIPYARD_BASE = new ShipyardBase();
	public static final Block SHIPYARD_TRUSS = new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL).nonOpaque());
	public static final ShipyardController SHIPYARD_CONTROLLER = new ShipyardController();

	// Ship
	public static final Thruster SMALL_THRUSTER = new Thruster(4.0 * 4.0 * 4.0 * 3.0);
	public static final Thruster LARGE_THRUSTER = new Thruster(9.0 * 9.0 * 9.0 * 3.0);
}
