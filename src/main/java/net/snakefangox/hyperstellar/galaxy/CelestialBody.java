package net.snakefangox.hyperstellar.galaxy;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class CelestialBody {

	private double x, y;
	private double radius;
	private double orbitDistance;
	private double orbitSpeed;
	private double orbitAngle;
	private RegistryKey<World> orbit;
	private RegistryKey<World> body;
	private CelestialBody[] orbitingBodies;

	public void readFromNbt(NbtCompound nbt) {
	}

	public void writeToNbt(NbtCompound nbt) {
	}
}
