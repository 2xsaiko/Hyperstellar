package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Random;

public class Sector {

	private final SectorPos pos;
	private boolean generated;
	private RegistryKey<World> sectorSpace;
	private CelestialBody systemCenter;

	public Sector(SectorPos pos) {
		this.pos = pos;
	}

	public void generateIfEmpty(long seed) {
		if (generated) return;
		Random random = new Random(seed);

	}

	@Environment(EnvType.CLIENT)
	public void drawSector(MatrixStack matrices, double x, double y) {

	}

	public void writeToNbt(NbtCompound nbt) {
		nbt.putInt("pos", pos.getIndex());
		nbt.putBoolean("generated", generated);
	}

	public void readFromNbt(NbtCompound nbt) {
		generated = nbt.getBoolean("generated");
	}

	public SectorPos getPos() {
		return pos;
	}
}
