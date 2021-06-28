package net.snakefangox.hyperstellar.ships;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipProperties {
	private final Map<String, ShipProperty> properties = new HashMap<>();
	private final Map<String, ShipModifier> modifiers = new HashMap<>();

	public ShipProperties(ShipProperty... properties) {
		for (ShipProperty prop : properties)
			this.properties.put(prop.getName(), prop);
	}

	public boolean addProperty(ShipProperty property) {
		if (!properties.containsKey(property.getName())) {
			properties.put(property.getName(), property);
			return true;
		} else {
			return false;
		}
	}

	public boolean addModifier(ShipModifier modifier) {
		if (!modifiers.containsKey(modifier.getName())) {
			modifiers.put(modifier.getName(), modifier);

			for (ShipModifier.Modifier mod : modifier.getModifiers()) {
				if (properties.containsKey(mod.property()))
					properties.get(mod.property()).addModifier(mod);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean removeProperty(String propertyKey) {
		return properties.remove(propertyKey) != null;
	}

	public boolean removeModifier(String modifierKey) {
		if (modifiers.containsKey(modifierKey)) {
			ShipModifier modifier = modifiers.remove(modifierKey);

			for (ShipModifier.Modifier mod : modifier.getModifiers()) {
				if (properties.containsKey(mod.property()))
					properties.get(mod.property()).removeModifier(mod);
			}

			return true;
		} else {
			return false;
		}
	}

	public double get(String propertyName) {
		return properties.containsKey(propertyName) ? properties.get(propertyName).get() : 0;
	}

	public NbtCompound toNBT() {
		NbtCompound nbt = new NbtCompound();
		NbtCompound propsNbt = new NbtCompound();
		NbtCompound modsNbt = new NbtCompound();

		for (var entry : properties.entrySet())
			propsNbt.putDouble(entry.getKey(), entry.getValue().getRawValue());

		for (var entry : modifiers.entrySet()) {
			NbtList modNBT = new NbtList();
			for (var mod : entry.getValue().getModifiers()) {
				NbtCompound mNbt = new NbtCompound();
				mNbt.putString("property", mod.property());
				mNbt.putDouble("mod", mod.mod());
				mNbt.putInt("op", mod.op().ordinal());
				modNBT.add(modNBT.size(), mNbt);
			}

			modsNbt.put(entry.getKey(), modNBT);
		}

		nbt.put("properties", propsNbt);
		nbt.put("modifiers", modsNbt);
		return nbt;
	}

	public void fromNBT(NbtCompound nbt) {
		NbtCompound propsNbt = nbt.getCompound("properties");
		NbtCompound modsNbt = nbt.getCompound("modifiers");

		for (var name : propsNbt.getKeys())
			addProperty(new ShipProperty(name, propsNbt.getDouble(name)));

		for (var name : modsNbt.getKeys()) {
			NbtList modNBT = modsNbt.getList(name, NbtElement.COMPOUND_TYPE);
			List<ShipModifier.Modifier> mods = new ArrayList<>();

			for (int i = 0; i < modNBT.size(); i++) {
				NbtCompound mNbt = modNBT.getCompound(i);
				mods.add(new ShipModifier.Modifier(mNbt.getString("property"),
						ShipModifier.Operation.values()[mNbt.getInt("op")], mNbt.getDouble("mod")));
			}

			addModifier(new ShipModifier(name, mods.toArray(new ShipModifier.Modifier[0])));
		}
	}
}
