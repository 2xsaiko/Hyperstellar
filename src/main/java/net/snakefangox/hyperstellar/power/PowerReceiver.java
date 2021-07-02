package net.snakefangox.hyperstellar.power;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PowerReceiver {
	int maxPowerNeeded(World world, BlockState state, BlockPos pos);
	void providePower(World world, BlockState state, BlockPos pos, int amount);
	void setEnabled(World world, BlockState state, BlockPos pos, boolean enable);
}
