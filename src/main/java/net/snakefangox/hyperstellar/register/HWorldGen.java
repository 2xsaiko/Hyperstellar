package net.snakefangox.hyperstellar.register;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.world_gen.OrbitGenerator;
import net.snakefangox.hyperstellar.world_gen.SpaceGenerator;

public class HWorldGen {

	public static final RegistryKey<Biome> SPACE_BIOME = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Hyperstellar.MODID, "space"));

	public static void registerChunkGenerators() {
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(Hyperstellar.MODID, "space"), SpaceGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(Hyperstellar.MODID, "orbit"), OrbitGenerator.CODEC);
	}

}
