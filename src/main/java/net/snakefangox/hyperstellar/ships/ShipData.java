package net.snakefangox.hyperstellar.ships;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.hyperstellar.blocks.SeatBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShipData {

	public static final String HULL_INTEGRITY = "hull_integrity";
	public static final String MAX_HULL_INTEGRITY = "max_hull_integrity";
	public static final String SHIELD_INTEGRITY = "shield_integrity";
	public static final String MAX_SHIELD_INTEGRITY = "max_shield_integrity";
	public static final String SHIELD_RECHARGE = "shield_recharge";
	public static final String POWER = "power";
	public static final String TONNAGE = "tonnage";
	public static final String FORWARD_THRUST = "forward_thrust";
	public static final String SIDE_THRUST = "side_thrust";
	public static final String VERT_THRUST = "vertical_thrust";
	public static final String[] ALL_SHIP_PROPS;

	private String shipName;
	private Direction forward;
	private final ShipProperties properties;
	private final List<BlockPos> seatOffsets = new ArrayList<>();
	private final List<BlockPos> thrusterOffsetF = new ArrayList<>();
	private final List<BlockPos> thrusterOffsetS = new ArrayList<>();
	private final List<BlockPos> thrusterOffsetV = new ArrayList<>();

	public ShipData() {
		this.shipName = ShipSettings.getRandomShipName();
		properties = new ShipProperties(new ShipProperty(HULL_INTEGRITY, 0),
				new ShipProperty(MAX_HULL_INTEGRITY, 0),
				new ShipProperty(SHIELD_INTEGRITY, 0),
				new ShipProperty(MAX_SHIELD_INTEGRITY, 0),
				new ShipProperty(SHIELD_RECHARGE, 0),
				new ShipProperty(POWER, 0),
				new ShipProperty(TONNAGE, 0),
				new ShipProperty(FORWARD_THRUST, 0),
				new ShipProperty(SIDE_THRUST, 0),
				new ShipProperty(VERT_THRUST, 0));
	}

	public ShipData(NbtCompound nbt) {
		properties = new ShipProperties();
		fromNbt(nbt);
	}

	public double get(String propertyName) {
		return properties.get(propertyName);
	}

	public void setForwardDirection(Direction forward) {
		this.forward = forward;
	}

	public void addThruster(Direction dir, BlockPos pos) {
		if (dir.getAxis() == Direction.Axis.Y) {
			thrusterOffsetV.add(pos);
		} else if (dir.getAxis() == forward.getAxis()) {
			thrusterOffsetF.add(pos);
		} else {
			thrusterOffsetS.add(pos);
		}
	}

	public void addSeat(SeatBlock block, BlockPos pos) {
		if (block.canControl()) {
			seatOffsets.add(0, pos);
		} else {
			seatOffsets.add(pos);
		}
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	public Direction getForward() {
		return forward;
	}

	public ShipProperties getProperties() {
		return properties;
	}

	public List<BlockPos> getSeatOffsets() {
		return seatOffsets;
	}

	public List<BlockPos> getThrusterOffsetF() {
		return thrusterOffsetF;
	}

	public List<BlockPos> getThrusterOffsetS() {
		return thrusterOffsetS;
	}

	public List<BlockPos> getThrusterOffsetV() {
		return thrusterOffsetV;
	}

	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putString("name", shipName);
		nbt.putInt("forward", forward.ordinal());
		nbt.put("properties", properties.toNBT());
		nbt.putLongArray("seats", seatOffsets.stream().map(BlockPos::asLong).collect(Collectors.toList()));
		nbt.putLongArray("thrustersF", thrusterOffsetF.stream().map(BlockPos::asLong).collect(Collectors.toList()));
		nbt.putLongArray("thrustersV", thrusterOffsetV.stream().map(BlockPos::asLong).collect(Collectors.toList()));
		nbt.putLongArray("thrustersS", thrusterOffsetS.stream().map(BlockPos::asLong).collect(Collectors.toList()));
		return nbt;
	}

	public void fromNbt(NbtCompound nbt) {
		shipName = nbt.getString("name");
		forward = Direction.values()[nbt.getInt("forward")];
		properties.fromNBT(nbt.getCompound("properties"));
		seatOffsets.clear();
		Arrays.stream(nbt.getLongArray("seats")).mapToObj(BlockPos::fromLong).forEach(seatOffsets::add);
		thrusterOffsetF.clear();
		Arrays.stream(nbt.getLongArray("thrustersF")).mapToObj(BlockPos::fromLong).forEach(thrusterOffsetF::add);
		thrusterOffsetV.clear();
		Arrays.stream(nbt.getLongArray("thrustersV")).mapToObj(BlockPos::fromLong).forEach(thrusterOffsetV::add);
		thrusterOffsetS.clear();
		Arrays.stream(nbt.getLongArray("thrustersS")).mapToObj(BlockPos::fromLong).forEach(thrusterOffsetS::add);
	}

	public String shipReport() {
		StringBuilder report = new StringBuilder();

		if (get(TONNAGE) <= 0)
			return report.append("Ship does not seem to exist\nCheck for theft").toString();

		report.append("Ship Name: ").append(shipName).append('\n');

		for (var prop : ALL_SHIP_PROPS)
			report.append(toReadable(prop)).append(": ").append(get(prop)).append('\n');

		if (seatOffsets.isEmpty())
			report.append("Ship has no control seats\n");

		if (get(VERT_THRUST) <= 0 || get(FORWARD_THRUST) <= 0 || get(SIDE_THRUST) <= 0)
			report.append("Ship has no thrust in one or more axes\nManeuvering will be difficult\n");

		if (get(TONNAGE) >= get(VERT_THRUST))
			report.append("Ship may be unable to take off in standard gravity\n");

		if (get(TONNAGE) >= get(FORWARD_THRUST) || get(TONNAGE) >= get(SIDE_THRUST))
			report.append("Ship may be slow to accelerate\n");

		if (get(POWER) <= 0)
			report.append("Ship is missing power source\n");

		if (get(MAX_SHIELD_INTEGRITY) > 0 && get(SHIELD_RECHARGE) <= 0)
			report.append("Ship has capacity for shields but no way to charge them\n");

		if (get(MAX_SHIELD_INTEGRITY) <= 0 && get(SHIELD_RECHARGE) > 0)
			report.append("Ship has shield recharge but no capacity\n");

		return report.toString();
	}

	private static String toReadable(String s) {
		return toTitleCase(s.replace("_", " "));
	}

	private static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder(input.length());
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			titleCase.append(c);
		}

		return titleCase.toString();
	}

	static {
		ALL_SHIP_PROPS = new String[]{HULL_INTEGRITY, MAX_HULL_INTEGRITY, SHIELD_INTEGRITY, MAX_SHIELD_INTEGRITY, SHIELD_RECHARGE,
				POWER, TONNAGE, FORWARD_THRUST, SIDE_THRUST, VERT_THRUST};
	}
}
