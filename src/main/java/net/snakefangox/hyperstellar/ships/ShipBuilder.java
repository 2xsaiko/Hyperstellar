package net.snakefangox.hyperstellar.ships;

import java.util.Iterator;

import net.snakefangox.hyperstellar.blocks.SeatBlock;
import net.snakefangox.hyperstellar.blocks.ShipNameplate;
import net.snakefangox.hyperstellar.blocks.Thruster;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;
import net.snakefangox.worldshell.storage.LocalSpace;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class ShipBuilder implements Iterator<BlockPos> {
	public static final double MAX_WEIGHT = 50;
	public static final double MAX_HULL = 200;

	private final ShipData shipData = new ShipData();
	private final Iterator<BlockPos> iterator;
	private final BlockView world;
	private final LocalSpace space;
	private long totalX, totalY, totalZ;
	private int count;

	public ShipBuilder(BlockView world, LocalSpace space, BlockBox box, Direction forwards) {
		this.world = world;
		this.space = space;
		iterator = BlockPos.iterate(box.getMinX(), box.getMinY(), box.getMinZ(),
				box.getMaxX(), box.getMaxY(), box.getMaxZ()).iterator();
		shipData.setForwardDirection(forwards);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public BlockPos next() {
		BlockPos pos = iterator.next();
		accept(world.getBlockState(pos), pos);
		return pos;
	}

	public void accept(BlockState state, BlockPos pos) {
		if (state.isAir()) return;

		++count;
		totalX += pos.getX();
		totalY += pos.getY();
		totalZ += pos.getZ();

		Block block = state.getBlock();
		if (block instanceof ShipPropertyProvider) {
			for (var prop : ShipData.ALL_SHIP_PROPS) {
				double change = ((ShipPropertyProvider) block).rawPropChange(prop, state, shipData.getForward());
				if (change != 0) {
					shipData.getProperties().addRaw(prop, change);
				}
			}
		} else if (block instanceof ShipNameplate) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ShipNameplateBE) {
				shipData.setShipName(((ShipNameplateBE) blockEntity).getName());
			}
		}

		if (block instanceof Thruster) {
			shipData.addThruster(state.get(Properties.FACING), space.toLocal(pos.toImmutable()));
		}
		if (block instanceof SeatBlock) shipData.addSeat((SeatBlock) block, space.toLocal(pos.toImmutable()));

		double weight = Math.min(state.getHardness(world, pos), MAX_WEIGHT);
		double hull = Math.min(block.getBlastResistance(), MAX_HULL);
		shipData.getProperties().addRaw(ShipData.TONNAGE, weight);
		shipData.getProperties().addRaw(ShipData.HULL_INTEGRITY, hull);
		shipData.getProperties().addRaw(ShipData.MAX_HULL_INTEGRITY, hull);
	}

	public ShipData getShipData() {
		return shipData;
	}

	public BlockPos getCentreOfGravity() {
		return new BlockPos(totalX / (float) count, totalY / (float) count, totalZ / (float) count);
	}

	public void loadShipData(ShipEntity shipEntity) {
		shipEntity.setShipData(shipData);
	}

	public int getCount() {
		return count;
	}
}
