package net.snakefangox.hyperstellar.galaxy;

import java.util.HashSet;
import java.util.Random;
import java.util.function.Consumer;

import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.util.WordMarkovChain;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Sector {

	public static final RegistryKey<DimensionType> SECTOR_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(Hyperstellar.MODID, "space"));
	public static final WordMarkovChain NAME_GEN;

	static {
		NAME_GEN = new WordMarkovChain("Ichnaea Ara Anadeia Carina Perseus Perileos Nebula Seashell Cloud Shield " +
				"Crux Aurigae Lambda Amphiaraus Centaurus Serpent Hurricane Argon Archon Timis " +
				"Perileos Galaxy Apus Majoris Lyra Eridani Sagitta Peleus Theta Solymus Corona " +
				"Nemesis Phoroneus Nebula Pavo Nemesis Miriandynus Canis Phoroneus Crux Myrmidon " +
				"Corona Kentaurus Aquila Euthenia Zephyrus Nebula");
	}

	private final SectorPos pos;
	private final Consumer<CelestialBody> registerBody;
	private boolean generated;
	private GalaxyDim sectorSpace;
	private CelestialBody systemCenter;

	public Sector(SectorPos pos, Consumer<CelestialBody> registerBody) {
		this.pos = pos;
		this.registerBody = registerBody;
	}

	public void generateIfEmpty(MinecraftServer server, long seed, @Nullable RegistryKey<World> linkWorld, Galaxy galaxy) {
		if (generated) return;
		Random random = new Random(seed);
		boolean isEmpty = random.nextFloat() < 0.2 && linkWorld != null;

		var name = generateName(random, galaxy);

		var ops = GalaxyDim.getOrbitDimensionOptions(random, server, SECTOR_TYPE, false);
		var key = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Hyperstellar.MODID, name));

		sectorSpace = new GalaxyDim(key, ops);

		if (!isEmpty) {
			systemCenter = new CelestialBody(0, name, new HashSet<>(), this, null, random, linkWorld, server);
		}

		loadWorld(server);
		generated = true;
	}

	private String generateName(Random random, Galaxy galaxy) {
		String name;
		RegistryKey<World> sectorKey;
		Random rand = new Random(random.nextLong());
		do {
			if (rand.nextBoolean()) {
				name = NAME_GEN.generate(rand, 8, 0.95f) + "-" + NAME_GEN.generate(rand, 8, 0.95f);
			} else {
				name = NAME_GEN.generate(rand, 15, 0.95f);
			}
			sectorKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Hyperstellar.MODID, name));
		} while (galaxy.sectorDimKeyExists(sectorKey));

		return name;
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

	public RegistryKey<World> getOrbitOrSelfAtPos(double x, double y) {
		if (systemCenter != null) {
			return systemCenter.getOrbitAt(x, y).orElse(sectorSpace.getWorldKey());
		}
		return sectorSpace.getWorldKey();
	}

	public void registerBody(CelestialBody body) {
		registerBody.accept(body);
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
			systemCenter = new CelestialBody(this, null);
			systemCenter.readFromNbt(nbt);
		}
	}

	public SectorPos getPos() {
		return pos;
	}
}
