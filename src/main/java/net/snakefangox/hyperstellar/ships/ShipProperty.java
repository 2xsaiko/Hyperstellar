package net.snakefangox.hyperstellar.ships;

import java.util.ArrayList;
import java.util.List;

public class ShipProperty {
	private final String name;
	private final List<ShipModifier.Modifier> modifiers = new ArrayList<>();
	private double rawValue;
	private double value;

	public ShipProperty(String name, double rawValue) {
		this.name = name;
		rawValue = Math.max(0, rawValue);
		this.rawValue = rawValue;
		value = rawValue;
	}

	public void addModifier(ShipModifier.Modifier modifier) {
		modifiers.add(modifier);
		reCalcValue();
	}

	public void removeModifier(ShipModifier.Modifier modifier) {
		if (modifiers.remove(modifier)) {
			reCalcValue();
		}
	}

	private void reCalcValue() {
		modifiers.sort(null);
		value = rawValue;
		for (ShipModifier.Modifier mod : modifiers) {
			value = mod.op().apply(value, mod.mod());
		}
		value = Math.max(0, value);
	}

	public String getName() {
		return name;
	}

	public double getRawValue() {
		return rawValue;
	}

	public void setRawValue(double rawValue) {
		rawValue = Math.max(0, rawValue);
		this.rawValue = rawValue;
		reCalcValue();
	}

	public double get() {
		return value;
	}
}
