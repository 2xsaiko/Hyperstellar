package net.snakefangox.hyperstellar.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PowerUtil {
	public static List<BlockPos> getNearbyPowerDemands(World world, BlockPos pos, int radius) {
		var iter = BlockPos.iterateOutwards(pos, radius, radius, radius);
		List<BlockPos> found = new ArrayList<>();

		for (BlockPos dest : iter) {
			if (world.getBlockState(dest).getBlock() instanceof PowerReceiver)
				found.add(dest.subtract(pos));
		}

		return found;
	}
}
