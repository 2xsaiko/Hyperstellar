package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.structures.ShipyardStructure;

public class HStructures {
	public static final StructurePieceType SHIPYARD_PIECE = ShipyardStructure.ShipyardPiece::new;
	private static final StructureFeature<DefaultFeatureConfig> SHIPYARD_STRUCTURE = new ShipyardStructure(DefaultFeatureConfig.CODEC);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SHIPYARD_CONFIGURED = SHIPYARD_STRUCTURE.configure(DefaultFeatureConfig.DEFAULT);


	public static void addStructures() {
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(Hyperstellar.MODID, "shipyard_piece"), SHIPYARD_PIECE);
		FabricStructureBuilder.create(new Identifier(Hyperstellar.MODID, "shipyard_structure"), SHIPYARD_STRUCTURE)
				.step(GenerationStep.Feature.SURFACE_STRUCTURES).defaultConfig(16, 8, 3576843).adjustsSurface()
				.superflatFeature(SHIPYARD_CONFIGURED).register();
		RegistryKey<ConfiguredStructureFeature<?, ?>> configuredShipyardKey = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY,
				new Identifier(Hyperstellar.MODID, "shipyard_structure"));
		BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, configuredShipyardKey.getValue(), SHIPYARD_CONFIGURED);
		BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.PLAINS), configuredShipyardKey);
	}
}
