package net.snakefangox.hyperstellar.ships;

import net.minecraft.nbt.NbtCompound;

public class ShipData {

	public static final String HULL_INTEGRITY = "hull_integrity";
	public static final String MAX_HULL_INTEGRITY = "max_hull_integrity";
	public static final String SHIELD_INTEGRITY = "shield_integrity";
	public static final String MAX_SHIELD_INTEGRITY = "max_shield_integrity";
	public static final String TONNAGE = "tonnage";
	public static final String THRUST = "thrust";
	public static final String FORWARD_THRUST_FRAC = "forward_thrust";
	public static final String SIDE_THRUST_FRAC = "side_thrust";
	public static final String VERT_THRUST_FRAC = "vert_thrust";

	private String shipName;
	private final ShipProperties properties;

	public ShipData(String shipName, double hullIntegrity, double shieldIntegrity, double tonnage, double thrust,
					double forwardThrustFrac, double sideThrustFrac, double verticalThrustFrac) {
		this.shipName = shipName;
		properties = new ShipProperties(new ShipProperty(HULL_INTEGRITY, hullIntegrity),
				new ShipProperty(MAX_HULL_INTEGRITY, hullIntegrity),
				new ShipProperty(SHIELD_INTEGRITY, shieldIntegrity),
				new ShipProperty(MAX_SHIELD_INTEGRITY, shieldIntegrity),
				new ShipProperty(TONNAGE, tonnage),
				new ShipProperty(THRUST, thrust),
				new ShipProperty(FORWARD_THRUST_FRAC, forwardThrustFrac),
				new ShipProperty(SIDE_THRUST_FRAC, sideThrustFrac),
				new ShipProperty(VERT_THRUST_FRAC, verticalThrustFrac));
	}

	public ShipData(NbtCompound nbt) {
		properties = new ShipProperties();
		fromNbt(nbt);
	}

	public String getShipName() {
		return shipName;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putString("name", shipName);
		nbt.put("properties", properties.toNBT());
		return nbt;
	}

	public void fromNbt(NbtCompound nbt) {
		shipName = nbt.getString("name");
		properties.fromNBT(nbt.getCompound("properties"));
	}
}
