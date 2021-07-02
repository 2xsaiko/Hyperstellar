package net.snakefangox.hyperstellar.blocks.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.hyperstellar.register.HEntities;

public class GalacticMapBE extends BlockEntity {
	public GalacticMapBE(BlockPos pos, BlockState state) {
		super(HEntities.GALACTIC_MAP, pos, state);
	}
}
