package net.snakefangox.hyperstellar.ships;

import java.util.ArrayList;
import java.util.List;

public class ShipProperty {
	private final String name;
	private double rawValue;
	private double value;
	private final List<ShipModifier.Modifier> modifiers = new ArrayList<>();

	public ShipProperty(String name, double rawValue) {
		this.name = name;
		this.rawValue = rawValue;
		value = rawValue;
	}

	public void addModifier(ShipModifier.Modifier modifier) {
		modifiers.add(modifier);
		reCalcValue();
	}

	public void removeModifier(ShipModifier.Modifier modifier) {
		if (modifiers.remove(modifier))
			reCalcValue();
	}

	private void reCalcValue() {
		modifiers.sort(null);
		value = rawValue;
		for (ShipModifier.Modifier mod : modifiers)
			value = mod.op().apply(value, mod.mod());
	}

	public String getName() {
		return name;
	}

	public double getRawValue() {
		return rawValue;
	}

	public double get() {
		return value;
	}

	public void setRawValue(double rawValue) {
		this.rawValue = rawValue;
		reCalcValue();
	}
}
