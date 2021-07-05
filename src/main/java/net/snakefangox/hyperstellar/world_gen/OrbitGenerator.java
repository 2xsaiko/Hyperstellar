package net.snakefangox.hyperstellar.world_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class OrbitGenerator extends ChunkGenerator {
	public static final Codec<OrbitGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BiomeSource.CODEC.fieldOf("biome_source")
							.forGetter(ChunkGenerator::getBiomeSource),
					StructuresConfig.CODEC.fieldOf("structures")
							.forGetter(ChunkGenerator::getStructuresConfig),
					Codec.LONG.fieldOf("seed").stable()
							.forGetter(spaceGenerator -> spaceGenerator.worldSeed)
			).apply(instance, instance.stable(OrbitGenerator::new))
	);

	private final long worldSeed;

	public OrbitGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed) {
		super(biomeSource, biomeSource, structuresConfig, worldSeed);
		this.worldSeed = worldSeed;
	}


	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new SpaceGenerator(biomeSource, getStructuresConfig(), seed);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView world) {
		return 0;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return new VerticalBlockSample(-1, new BlockState[0]);
	}
}
