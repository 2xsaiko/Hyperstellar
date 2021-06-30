package net.snakefangox.hyperstellar.ships;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.snakefangox.hyperstellar.blocks.SeatBlock;
import net.snakefangox.hyperstellar.blocks.ShipNameplate;
import net.snakefangox.hyperstellar.blocks.Thruster;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;
import net.snakefangox.worldshell.storage.LocalSpace;

import java.util.Iterator;

public class ShipBuilder implements Iterator<BlockPos> {
	public static final double MAX_WEIGHT = 50;
	public static final double MAX_HULL = 200;

	private final ShipData shipData = new ShipData();
	private final BlockView world;
	private final LocalSpace space;
	private final BlockBox box;
	private final BlockPos.Mutable currentPos = new BlockPos.Mutable();
	private final BlockPos.Mutable nextPos = new BlockPos.Mutable();

	public ShipBuilder(BlockView world, LocalSpace space, BlockBox box, Direction forwards) {
		this.world = world;
		this.space = space;
		this.box = box;
		shipData.setForwardDirection(forwards);
		currentPos.set(box.getMinX(), box.getMinY(), box.getMinZ());
		nextPos.set(box.getMinX(), box.getMinY(), box.getMinZ());
	}

	@Override
	public boolean hasNext() {
		if (world.getBlockState(currentPos).isAir()) {
			nextPos.set(currentPos);
			advance(nextPos);
			while (world.getBlockState(currentPos).isAir() &&
				   (nextPos.getX() <= box.getMaxX() || nextPos.getY() <= box.getMaxY() || nextPos.getZ() <= box.getMaxZ())) {
				advance(nextPos);
			}
			currentPos.set(nextPos);
		}
		return currentPos.getX() <= box.getMaxX() || currentPos.getY() <= box.getMaxY() || currentPos.getZ() <= box.getMaxZ();
	}

	@Override
	public BlockPos next() {
		advance(currentPos);
		accept(world.getBlockState(currentPos), currentPos.toImmutable());
		return currentPos;
	}

	private void advance(BlockPos.Mutable pos) {
		if (pos.getX() > box.getMaxX()) {
			pos.setX(box.getMinX());
			pos.setY(pos.getY() + 1);
		}
		if (pos.getY() > box.getMaxY()) {
			pos.setY(box.getMinY());
			pos.setZ(pos.getZ() + 1);
		}
		pos.setX(pos.getX() + 1);
	}

	public void accept(BlockState state, BlockPos pos) {
		if (state.isAir()) return;

		Block block = state.getBlock();
		if (block instanceof ShipPropertyProvider) {
			for (var prop : ShipData.ALL_SHIP_PROPS) {
				double change = ((ShipPropertyProvider) block).rawPropChange(prop, state, shipData.getForward());
				if (change != 0)
					shipData.getProperties().addRaw(prop, change);
			}
		} else if (block instanceof ShipNameplate) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShipNameplateBE)
				shipData.setShipName(((ShipNameplateBE) blockEntity).getName());
		}

		if (block instanceof Thruster) shipData.addThruster(state.get(Properties.FACING), space.toLocal(pos));
		if (block instanceof SeatBlock) shipData.addSeat((SeatBlock) block, space.toLocal(pos));

		double weight = Math.min(state.getHardness(world, pos), MAX_WEIGHT);
		double hull = Math.min(block.getBlastResistance(), MAX_HULL);
		shipData.getProperties().addRaw(ShipData.TONNAGE, weight);
		shipData.getProperties().addRaw(ShipData.HULL_INTEGRITY, hull);
		shipData.getProperties().addRaw(ShipData.MAX_HULL_INTEGRITY, hull);
	}

	public ShipData getShipData() {
		return shipData;
	}

	public void loadShipData(ShipEntity shipEntity) {
		shipEntity.setShipData(shipData);
	}
}
