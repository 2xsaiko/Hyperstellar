package net.snakefangox.hyperstellar.ships;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.snakefangox.hyperstellar.mixin.AccessDataTracker;
import net.snakefangox.worldshell.WSNetworking;
import net.snakefangox.worldshell.entity.WorldShellEntity;
import net.snakefangox.worldshell.math.Quaternion;
import net.snakefangox.worldshell.math.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShipEntity extends WorldShellEntity {

	private static final float YAW_LIMIT = 125;

	private static final TrackedData<Map<UUID, Integer>> PASSENGER_SEATS = DataTracker.registerData(ShipEntity.class, ShipDatatrackers.PASSENGER_MAP);
	private static final TrackedData<ShipData> SHIP_DATA = DataTracker.registerData(ShipEntity.class, ShipDatatrackers.SHIP_DATA);
	private static final TrackedData<Quaternion> TARGET_ROTATION = DataTracker.registerData(ShipEntity.class, WSNetworking.QUATERNION);

	private int nextSeatId;

	public ShipEntity(EntityType<?> type, World world) {
		super(type, world, ShipSettings.SETTINGS);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		getDataTracker().startTracking(PASSENGER_SEATS, new HashMap<>());
		getDataTracker().startTracking(SHIP_DATA, new ShipData());
		getDataTracker().startTracking(TARGET_ROTATION, new Quaternion());
	}

	@Override
	public void tick() {
		super.tick();
		handleRotation();
		handleMovement();
	}

	private void handleRotation() {
		Entity captain = getPrimaryPassenger();
		if (captain != null) {
			Quaternion captainLook = new Quaternion().fromAngles(0, -Math.toRadians(captain.getYaw()) + Math.PI, 0);
			if (Math.abs(getTargetRotation().dot(captainLook)) != 1d)
				setTargetRotation(captainLook);
		}

		Quaternion rot = getRotation();
		Quaternion target = getTargetRotation();

		setRotation(new Quaternion().set(rot).slerp(target, getRotSpeed()));
	}

	private void handleMovement() {
		Entity captain = getPrimaryPassenger();
		if (captain != null) {
			Vec3d capRawVel = captain.getVelocity().normalize();
			Vector3d capVel = new Vector3d(capRawVel.x, capRawVel.y, capRawVel.z).multLocal(getMoveSpeed());
			setVelocity(capVel.x, 0, capVel.z);
		}
		move(MovementType.SELF, getVelocity());
	}

	private double getRotSpeed() {
		return MathHelper.clamp(getShipData().get(ShipData.SIDE_THRUST) / getShipData().get(ShipData.TONNAGE), 0, 0.8);
	}

	private double getMoveSpeed() {
		return MathHelper.clamp(getShipData().get(ShipData.FORWARD_THRUST) / getShipData().get(ShipData.TONNAGE), 0, 20);
	}

	private double getLift() {
		return MathHelper.clamp(getShipData().get(ShipData.VERT_THRUST) / getShipData().get(ShipData.TONNAGE), 0, 20);
	}

	public void nextToRide(BlockPos seatPos) {
		nextSeatId = getShipData().getSeatOffsets().indexOf(seatPos);
		if (nextSeatId == -1) nextSeatId = getShipData().getSeatOffsets().size() - 1;
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		/*for (var passenger : getPassengerList())
			if (isCaptain(passenger)) return passenger;*/
		return getFirstPassenger();
	}

	private boolean isCaptain(Entity passenger) {
		Integer seatId = getPassengerSeats().get(passenger.getUuid());
		return seatId != null && seatId == 0;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		getPassengerSeats().put(passenger.getUuid(), nextSeatId);
		refreshPassengerSeats();
		passenger.setYaw(getYaw());
		super.addPassenger(passenger);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return getPassengerList().size() < getShipData().getSeatOffsets().size();
	}

	@Override
	protected void removePassenger(Entity passenger) {
		getPassengerSeats().remove(passenger.getUuid());
		refreshPassengerSeats();
		super.removePassenger(passenger);
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
		if (this.hasPassenger(passenger)) {
			int seatId = getPassengerSeats().get(passenger.getUuid());
			if (seatId == -1) return;
			BlockPos seatOffset = getShipData().getSeatOffsets().get(seatId);
			double dX = seatOffset.getX() + 0.5;
			double dY = seatOffset.getY() + passenger.getHeightOffset() + 0.5;
			double dZ = seatOffset.getZ() + 0.5;
			Vector3d vec = toGlobal(new Vector3d(dX, dY, dZ));
			positionUpdater.accept(passenger, vec.x, vec.y, vec.z);
		}
	}

	@Override
	public void onPassengerLookAround(Entity passenger) {
		float yaw = getYaw();
		passenger.setBodyYaw(yaw);
		float f = MathHelper.wrapDegrees(passenger.getYaw() - yaw);
		float g = MathHelper.clamp(f, -YAW_LIMIT, YAW_LIMIT);
		passenger.prevYaw += g - f;
		passenger.setYaw(passenger.getYaw() + g - f);
		passenger.setHeadYaw(passenger.getYaw());
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		return passenger.getPos();
	}

	@Override
	public float getYaw() {
		Quaternion rot = getRotation();
		Vector3d dir = rot.multLocal(new Vector3d(0, 0, -1));
		return (float) Math.toDegrees(-Math.atan2(dir.x, dir.z));
	}

	@Override
	public float getPitch() {
		return (float) Math.toDegrees(getRotation().getPitch());
	}

	protected void setShipData(ShipData shipData) {
		getDataTracker().set(SHIP_DATA, shipData);
	}

	public ShipData getShipData() {
		return getDataTracker().get(SHIP_DATA);
	}

	private void refreshShipData() {
		((AccessDataTracker) getDataTracker()).invokeGetEntry(SHIP_DATA).setDirty(true);
		((AccessDataTracker) getDataTracker()).setDirty(true);
	}

	private Map<UUID, Integer> getPassengerSeats() {
		return getDataTracker().get(PASSENGER_SEATS);
	}

	private void refreshPassengerSeats() {
		((AccessDataTracker) getDataTracker()).invokeGetEntry(PASSENGER_SEATS).setDirty(true);
		((AccessDataTracker) getDataTracker()).setDirty(true);
	}

	protected void setTargetRotation(Quaternion targetRotation) {
		getDataTracker().set(TARGET_ROTATION, targetRotation);
	}

	public Quaternion getTargetRotation() {
		return getDataTracker().get(TARGET_ROTATION);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		setShipData(new ShipData(tag.getCompound("shipData")));
		NbtCompound seatTag = tag.getCompound("seats");
		var passengerSeats = getPassengerSeats();
		for (var key : seatTag.getKeys())
			passengerSeats.put(UUID.fromString(key), seatTag.getInt(key));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		tag.put("shipData", getShipData().toNbt());
		var seatTag = new NbtCompound();
		var passengerSeats = getPassengerSeats();
		for (var entry : passengerSeats.entrySet())
			seatTag.putInt(entry.getKey().toString(), entry.getValue());
		tag.put("seats", seatTag);
	}
}
