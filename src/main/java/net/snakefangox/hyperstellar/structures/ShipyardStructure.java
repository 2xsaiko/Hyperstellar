package net.snakefangox.hyperstellar.structures;

import java.util.Random;

import com.mojang.serialization.Codec;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.register.HStructures;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ShipyardStructure extends StructureFeature<DefaultFeatureConfig> {
	public ShipyardStructure(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> feature, ChunkPos pos, int references, long seed) {
			super(feature, pos, references, seed);
		}

		@Override
		public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, ChunkPos pos, Biome biome, DefaultFeatureConfig config, HeightLimitView world) {
			int x = pos.x * 16;
			int z = pos.z * 16;
			int y = chunkGenerator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG, world);
			BlockPos blockPos = new BlockPos(x, y, z);
			BlockRotation rotation = BlockRotation.random(this.random);
			StructurePlacementData data = new StructurePlacementData();
			data.setRotation(rotation).setPosition(blockPos).setRandom(random);
			children.add(new ShipyardPiece(manager, data, blockPos));
			this.setBoundingBoxFromChildren();
		}
	}

	public static class ShipyardPiece extends GeneralPiece {
		public static final Identifier ID = new Identifier(Hyperstellar.MODID, "shipyard");

		public ShipyardPiece(StructureManager structureManager, StructurePlacementData placementData, BlockPos pos) {
			super(HStructures.SHIPYARD_PIECE, structureManager, ID, placementData, pos);
		}

		public ShipyardPiece(ServerWorld world, NbtCompound nbtCompound) {
			super(HStructures.SHIPYARD_PIECE, world, nbtCompound);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {

		}
	}
}
