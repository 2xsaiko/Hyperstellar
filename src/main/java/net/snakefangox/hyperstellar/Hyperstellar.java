package net.snakefangox.hyperstellar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.snakefangox.hyperstellar.galaxy.GalaxyLogic;
import net.snakefangox.hyperstellar.register.*;
import net.snakefangox.hyperstellar.world_gen.SpaceGenerator;
import net.snakefangox.rapidregister.RapidRegister;
import net.snakefangox.worldshell.util.DynamicWorldRegister;
import net.snakefangox.worldshell.world.CreateWorldsEvent;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

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
