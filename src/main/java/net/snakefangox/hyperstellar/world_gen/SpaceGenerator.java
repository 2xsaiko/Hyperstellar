package net.snakefangox.hyperstellar.world_gen;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.snakefangox.hyperstellar.register.HBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class SpaceGenerator extends ChunkGenerator {
	public static final Codec<SpaceGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BiomeSource.CODEC.fieldOf("biome_source")
							.forGetter(ChunkGenerator::getBiomeSource),
					StructuresConfig.CODEC.fieldOf("structures")
							.forGetter(ChunkGenerator::getStructuresConfig),
					Codec.LONG.fieldOf("seed").stable()
							.forGetter(spaceGenerator -> spaceGenerator.worldSeed)
			).apply(instance, instance.stable(SpaceGenerator::new))
	);

	private final long worldSeed;
	private final OpenSimplexNoise noise1;
	private final OpenSimplexNoise noise2;
	private final OpenSimplexNoise noise3;
	private final OpenSimplexNoise craterNoise;

	public SpaceGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed) {
		super(biomeSource, biomeSource, structuresConfig, worldSeed);
		this.worldSeed = worldSeed;
		Random random = new Random(worldSeed);
		this.noise1 = new OpenSimplexNoise(random.nextLong());
		this.noise2 = new OpenSimplexNoise(random.nextLong());
		this.noise3 = new OpenSimplexNoise(random.nextLong());
		this.craterNoise = new OpenSimplexNoise(random.nextLong());
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
		int startX = chunk.getPos().getStartX();
		int startZ = chunk.getPos().getStartZ();

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = -256; y < 256; y++) {
					mutable.set(x, y, z);

					double noise = noise(startX + x, y, startZ + z);

					if (noise > 0.75) {
						chunk.setBlockState(mutable, HBlocks.ASTEROID_ROCK.getDefaultState(), false);
					}
				}
			}
		}

		return CompletableFuture.completedFuture(chunk);
	}

	private double noise(int x, int y, int z) {
		double noise = 0;

		noise += this.noise1.sample(x / 150.0, y / 150.0, z / 150.0);
		noise += this.noise2.sample(x / 100.0, y / 100.0, z / 100.0) / 2.0;
		noise += this.noise3.sample(x / 50.0, y / 50.0, z / 50.0) / 4.0;

		noise -= MathHelper.clamp(Math.pow(this.craterNoise.sample(x / 10.0, y / 10.0, z / 10.0) * 1.25, 2), 0, 1) * 0.05;

		noise -= (12.0 / (y + 257.0)) - (12.0 / (y - 257.0)) - 0.16;

		return noise > 0 ? noise * 1.15 : noise;
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
