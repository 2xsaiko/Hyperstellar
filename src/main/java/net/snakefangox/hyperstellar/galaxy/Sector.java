package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.snakefangox.hyperstellar.Hyperstellar;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;

public class Sector {

	public static final RegistryKey<DimensionType> SECTOR_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(Hyperstellar.MODID, "space"));

	private final SectorPos pos;
	private boolean generated;
	private GalaxyDim sectorSpace;
	private CelestialBody systemCenter;

	public Sector(SectorPos pos) {
		this.pos = pos;
	}

	public void generateIfEmpty(MinecraftServer server, long seed, @Nullable RegistryKey<World> linkWorld) {
		if (generated) return;
		Random random = new Random(seed);
		boolean isEmpty = random.nextFloat() < 0.2 && linkWorld != null;

		var name = generateName(random);

		var ops = GalaxyDim.getOrbitDimensionOptions(random, server, SECTOR_TYPE);
		var key = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Hyperstellar.MODID, name));

		sectorSpace = new GalaxyDim(key, ops);

		if (!isEmpty)
			systemCenter = new CelestialBody(0, name, new HashSet<>(), null, random, linkWorld, server);

		loadWorld(server);
		generated = true;
	}

	private String generateName(Random random) {
		StringBuilder name = new StringBuilder();
		var len = 10 + random.nextInt(10);

		while (name.length() < len) {
			char ch = (char) random.nextInt(255);

			if (Character.isAlphabetic(ch)) name.append(ch);
		}

		return name.toString();
	}

	public void tickSector(double time) {
		if (systemCenter != null) systemCenter.tickBody(time);
	}

	public void loadWorld(MinecraftServer server) {
		sectorSpace.loadWorld(server);
		if (systemCenter != null) systemCenter.recursivelyLoad(server);
	}

	@Environment(EnvType.CLIENT)
	public void drawSector(MatrixStack matrices, double x, double y) {
		if (systemCenter != null) systemCenter.renderBody(matrices, x, y);
	}

	public GalaxyDim getSectorSpace() {
		return sectorSpace;
	}

	public CelestialBody getSystemCenter() {
		return systemCenter;
	}

	public void writeToNbt(NbtCompound nbt) {
		nbt.putInt("pos", pos.getIndex());
		nbt.putBoolean("generated", generated);
		nbt.put("sectorSpace", sectorSpace.writeNbt());
		NbtCompound systemNbt = new NbtCompound();
		if (systemCenter != null) systemCenter.writeToNbt(systemNbt);
		nbt.put("systemCenter", systemNbt);
	}

	public void readFromNbt(NbtCompound nbt) {
		generated = nbt.getBoolean("generated");
		sectorSpace = new GalaxyDim(nbt.getCompound("sectorSpace"));
		if (nbt.getKeys().contains("systemCenter")) {
			systemCenter = new CelestialBody(null);
			systemCenter.readFromNbt(nbt);
		}
	}

	public SectorPos getPos() {
		return pos;
	}
}
