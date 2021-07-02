package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.snakefangox.hyperstellar.blocks.*;
import net.snakefangox.hyperstellar.blocks.entities.BackupGeneratorBE;
import net.snakefangox.hyperstellar.blocks.entities.FusionGeneratorBE;
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
	public static final ShipNameplate SHIP_NAMEPLATE = new ShipNameplate();
	public static final Thruster SMALL_THRUSTER = new Thruster(4.0 * 4.0 * 4.0 * 1.5);
	public static final Thruster LARGE_THRUSTER = new Thruster(9.0 * 9.0 * 9.0 * 1.5);
	public static final ShieldBlock SHIELD_GENERATOR = new ShieldBlock(0, 10);
	public static final ShieldBlock SHIELD_CAPACITOR = new ShieldBlock(50, 0);
	public static final SeatBlock CAPTAINS_CHAIR = new SeatBlock(true);
	public static final SeatBlock CREW_CHAIR = new SeatBlock(false);

	// Power
	public static final PowerGenerator BACKUP_GENERATOR = new PowerGenerator(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL), BackupGeneratorBE::new);
	public static final PowerGenerator FUSION_REACTOR_CONTROLLER = new PowerGenerator(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL), FusionGeneratorBE::new);
	public static final Block FUSION_REACTOR_CHANNEL = new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	public static final Block FUSION_REACTOR_COIL = new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL));
}
