package net.snakefangox.hyperstellar.structures;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class GeneralPiece extends SimpleStructurePiece {

	public GeneralPiece(StructurePieceType type, StructureManager structureManager, Identifier identifier, StructurePlacementData placementData, BlockPos pos) {
		super(type, 0, structureManager, identifier, identifier.toString(), placementData, pos);
	}

	public GeneralPiece(StructurePieceType type, ServerWorld world, NbtCompound nbtCompound) {
		super(type, nbtCompound, world, identifier -> (new StructurePlacementData())
				.setRotation(BlockRotation.values()[nbtCompound.getInt("rotation")])
				.setMirror(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS));

	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		super.writeNbt(world, nbt);
		nbt.putInt("rotation", placementData.getRotation().ordinal());
	}
}
