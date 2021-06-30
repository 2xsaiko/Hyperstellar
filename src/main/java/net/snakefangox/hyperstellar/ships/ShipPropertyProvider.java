package net.snakefangox.hyperstellar.ships;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface ShipPropertyProvider {
	double rawPropChange(String propName, BlockState state, Direction forwards);
}
