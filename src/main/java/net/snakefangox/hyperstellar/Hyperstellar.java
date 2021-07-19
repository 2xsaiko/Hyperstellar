package net.snakefangox.hyperstellar;

import net.snakefangox.hyperstellar.galaxy.GalaxyLogic;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HClientPackets;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.register.HItems;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.register.HServerPackets;
import net.snakefangox.hyperstellar.register.HSounds;
import net.snakefangox.hyperstellar.register.HStructures;
import net.snakefangox.hyperstellar.register.HWorldGen;
import net.snakefangox.rapidregister.RapidRegister;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

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
		HClientPackets.registerClientPackets();
		GalaxyLogic.registerGalaxyLogic();
	}
}
