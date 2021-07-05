package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.world_gen.SpaceGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CelestialBody {

	public static final double MAX_RADIUS = 10000;
	public static final double MAX_SPEED = 360d / (20 * 60);
	public static final RegistryKey<DimensionType> ORBIT_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier(Hyperstellar.MODID, "orbit"));

	@Nullable
	private final CelestialBody parent;
	private double radius;
	private double orbitDistance;
	private double orbitSpeed;
	private double orbitAngle;
	private GalaxyDim orbit;
	@Nullable
	private GalaxyDim body;
	@Nullable
	private RegistryKey<World> linkedWorld;
	private CelestialBody[] orbitingBodies = new CelestialBody[0];

	public CelestialBody(@Nullable CelestialBody parent) {
		this.parent = parent;
	}

	public CelestialBody(double dist, String sectorName, Set<String> usedEnds, @Nullable CelestialBody parent,
						 Random random, @Nullable RegistryKey<World> linkedWorld, MinecraftServer server) {
		this.parent = parent;
		radius = Math.min(random.nextFloat(), 0.0001) * MAX_RADIUS;
		orbitDistance = dist + radius;

		orbitSpeed = Math.min(random.nextDouble(), 0.0001) * MAX_SPEED;
		orbitAngle = random.nextDouble() * 360d;

		var end = generateNameEnd(random, usedEnds);
		var key = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Hyperstellar.MODID, sectorName + "_" + end));
		var orbitKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Hyperstellar.MODID, sectorName + "_" + end + "orbit"));

		DimensionOptions orbitOps = GalaxyDim.getOrbitDimensionOptions(random, server, ORBIT_TYPE);
		orbit = new GalaxyDim(orbitKey, orbitOps);

		if (linkedWorld == null) {
			body = generateBody(key, server, random);

			var orbitingCount = random.nextInt(3) - usedEnds.size();
			usedEnds.add(end);

			if (orbitingCount > 0) {
				orbitingBodies = new CelestialBody[orbitingCount];
				for (int i = 0; i < orbitingCount; i++)
					orbitingBodies[i] = new CelestialBody(radius, sectorName, usedEnds, this, random, null, server);
			}
		} else {
			this.linkedWorld = linkedWorld;
		}
	}

	private GalaxyDim generateBody(RegistryKey<World> key, MinecraftServer server, Random random) {

		var validBiomes = server.getRegistryManager().get(Registry.BIOME_KEY).stream()
				.filter(biome -> !biome.getCategory().equals(Biome.Category.NONE)).collect(Collectors.toList());
		var biome = validBiomes.get(random.nextInt(validBiomes.size()));
		var source = new FixedBiomeSource(biome);
		var config = new StructuresConfig(Optional.empty(), Collections.emptyMap());
		var gen = new SpaceGenerator(source, config, random.nextLong());

		var type = createPlanetDimensionType(biome, random);
		Supplier<DimensionType> orbitTypeSupplier = () -> type;

		var ops = new DimensionOptions(orbitTypeSupplier, gen);

		return new GalaxyDim(key, ops);
	}

	private DimensionType createPlanetDimensionType(Biome biome, Random random) {
		return DimensionType.create(OptionalLong.empty(), true, false,
				biome.getTemperature() + random.nextFloat() > 3, false,
				1, false, false, true, true, true,
				0, 256, 256, DirectBiomeAccessType.INSTANCE, new Identifier(Hyperstellar.MODID, "overworld"),
				new Identifier(Hyperstellar.MODID, "overworld"), 0f);
	}

	private String generateNameEnd(Random random, Set<String> usedEnds) {
		StringBuilder end = new StringBuilder();
		while (true) {
			char ch = (char) random.nextInt(255);
			if (Character.isLetterOrDigit(ch)) end.append(ch);

			if (!usedEnds.contains(end.toString())) {
				return end.toString();
			}
		}
	}

	public void tickBody(double time) {
		orbitAngle += (time * orbitSpeed);
		if (orbitAngle >= 360d)
			orbitAngle = orbitAngle - 360d;

		for (var cBody : orbitingBodies)
			cBody.tickBody(time);
	}

	public Vec2d getPos() {
		Vec2d root = parent == null ? Vec2d.ZERO : parent.getPos();

		double x = 0;
		double y = orbitDistance;

		double theta = Math.toRadians(orbitAngle);
		double cs = Math.cos(theta);
		double sn = Math.sin(theta);

		double pX = x * cs - y * sn;
		double pY = x * cs - y * sn;

		return new Vec2d(root.x() + pX, root.y() + pY);
	}

	public void recursivelyLoad(MinecraftServer server) {
		orbit.loadWorld(server);
		if (body != null) body.loadWorld(server);

		for (var bod : orbitingBodies)
			bod.recursivelyLoad(server);
	}

	@Environment(EnvType.CLIENT)
	public void renderBody(MatrixStack matrices, double x, double y) {

	}

	public GalaxyDim getOrbit() {
		return orbit;
	}

	public void writeToNbt(NbtCompound nbt) {
		nbt.putDouble("radius", radius);
		nbt.putDouble("orbitDistance", orbitDistance);
		nbt.putDouble("orbitSpeed", orbitSpeed);
		nbt.putDouble("orbitAngle", orbitAngle);
		nbt.put("orbit", orbit.writeNbt());
		if (body != null) {
			nbt.put("body", body.writeNbt());
		} else if (linkedWorld != null) {
			nbt.putString("linkedWorld", linkedWorld.toString());
		}

		NbtList orbitingList = new NbtList();
		for (var body : orbitingBodies) {
			NbtCompound orbNbt = new NbtCompound();
			body.writeToNbt(orbNbt);
			orbitingList.add(orbNbt);
		}
		nbt.put("orbitingBodies", orbitingList);
	}

	public void readFromNbt(NbtCompound nbt) {
		radius = nbt.getDouble("radius");
		orbitDistance = nbt.getDouble("orbitDistance");
		orbitSpeed = nbt.getDouble("orbitSpeed");
		orbitAngle = nbt.getDouble("orbitAngle");
		orbit = new GalaxyDim(nbt.getCompound("orbit"));
		if (nbt.getKeys().contains("body")) {
			body = new GalaxyDim(nbt.getCompound("body"));
		} else if (nbt.getKeys().contains("linkedWorld")) {
			linkedWorld = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("linkedWorld")));
		}

		NbtList orbitingList = nbt.getList("orbitingBodies", NbtElement.COMPOUND_TYPE);
		orbitingBodies = new CelestialBody[orbitingList.size()];
		for (int i = 0; i < orbitingList.size(); i++) {
			NbtCompound bodyNbt = orbitingList.getCompound(i);
			CelestialBody nBody = new CelestialBody(this);
			nBody.readFromNbt(bodyNbt);
			orbitingBodies[i] = nBody;
		}
	}
}
