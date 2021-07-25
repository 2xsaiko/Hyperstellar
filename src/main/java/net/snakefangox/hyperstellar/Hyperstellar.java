package net.snakefangox.hyperstellar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.galaxy.GalaxyLogic;
import net.snakefangox.hyperstellar.register.*;
import net.snakefangox.rapidregister.RapidRegister;

public class Hyperstellar implements ModInitializer {

	public static final String MODID = "hyperstellar";

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"),
			() -> new ItemStack(HBlocks.TRITANIUM_BLOCK));

	@Override
	public void onInitialize() {
		// Register
		RapidRegister.register(MODID, HBlocks.class, HItems.class, HEntities.class, HSounds.class);
		HStructures.addStructures();
		HWorldGen.registerChunkGenerators();
		HScreens.registerScreens();
		HServerPackets.registerServerPackets();
		GalaxyLogic.registerGalaxyLogic();
	}
}
