package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

import java.util.Random;

public class Sector {

	private final SectorPos pos;
	private boolean generated;
	private GalaxyDim sectorSpace;
	private CelestialBody systemCenter;

	public Sector(SectorPos pos) {
		this.pos = pos;
	}

	public void generateIfEmpty(long seed) {
		if (generated) return;
		Random random = new Random(seed);
		boolean isEmpty = random.nextFloat() < 0.2;
		if (isEmpty) return;

		float centerType = random.nextFloat();

		if (centerType < 0.7) {

		}
	}

	public void loadWorld(MinecraftServer server) {
		sectorSpace.loadWorld(server);
		systemCenter.recursivelyLoad(server);
	}

	@Environment(EnvType.CLIENT)
	public void drawSector(MatrixStack matrices, double x, double y) {

	}

	public void writeToNbt(NbtCompound nbt) {
		nbt.putInt("pos", pos.getIndex());
		nbt.putBoolean("generated", generated);
		nbt.put("sectorSpace", sectorSpace.writeNbt());
		NbtCompound systemNbt = new NbtCompound();
		systemCenter.writeToNbt(systemNbt);
		nbt.put("systemCenter", systemNbt);
	}

	public void readFromNbt(NbtCompound nbt) {
		generated = nbt.getBoolean("generated");
		sectorSpace = new GalaxyDim(nbt.getCompound("sectorSpace"));
		systemCenter = new CelestialBody();
		systemCenter.readFromNbt(nbt);
	}

	public SectorPos getPos() {
		return pos;
	}
}
