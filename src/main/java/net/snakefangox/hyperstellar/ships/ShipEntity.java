package net.snakefangox.hyperstellar.ships;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.worldshell.entity.WorldShellEntity;
import net.snakefangox.worldshell.math.Vector3d;

public class ShipEntity extends WorldShellEntity {

	private ShipData shipData;

	public ShipEntity(EntityType<?> type, World world) {
		super(type, world, ShipSettings.SETTINGS);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return getPassengerList().size() < shipData.getSeatOffsets().size();
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
		if (this.hasPassenger(passenger)) {
			int seatIndex = getPassengerList().indexOf(passenger);
			BlockPos seatOffset = shipData.getSeatOffsets().get(seatIndex);
			double dX = seatOffset.getX() + 0.5;
			double dY = seatOffset.getY() + getMountedHeightOffset() + passenger.getHeightOffset();
			double dZ = seatOffset.getZ() + 0.5;
			Vector3d vec = toGlobal(new Vector3d(dX, dY, dZ));
			positionUpdater.accept(passenger, vec.x, vec.y, vec.z);
		}
	}

	protected void setShipData(ShipData shipData) {
		this.shipData = shipData;
	}

	public ShipData getShipData() {
		return shipData;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		shipData = new ShipData(tag.getCompound("shipData"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		tag.put("shipData", shipData.toNbt());
	}
}
