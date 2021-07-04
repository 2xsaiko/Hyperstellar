package net.snakefangox.hyperstellar.register;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.world_gen.SpaceGenerator;

public class HWorldGen {

	public static void registerChunkGenerators() {
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(Hyperstellar.MODID, "space"), SpaceGenerator.CODEC);
	}

}
