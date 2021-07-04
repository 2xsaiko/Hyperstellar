package net.snakefangox.hyperstellar.galaxy;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;

public class CelestialBody {
	private double centerX, centerY;
	private double radius;
	private double orbitDistance;
	private double orbitSpeed;
	private double orbitAngle;
	private GalaxyDim orbit;
	private GalaxyDim body;
	private CelestialBody[] orbitingBodies = new CelestialBody[0];

	public void recursivelyLoad(MinecraftServer server) {
		orbit.loadWorld(server);
		if (body != null) body.loadWorld(server);

		for (var bod : orbitingBodies)
			bod.recursivelyLoad(server);
	}

	public void writeToNbt(NbtCompound nbt) {
		nbt.putDouble("centerX", centerX);
		nbt.putDouble("centerY", centerY);
		nbt.putDouble("radius", radius);
		nbt.putDouble("orbitDistance", orbitDistance);
		nbt.putDouble("orbitSpeed", orbitSpeed);
		nbt.putDouble("orbitAngle", orbitAngle);
		nbt.put("orbit", orbit.writeNbt());
		if (body != null)
			nbt.put("body", body.writeNbt());

		NbtList orbitingList = new NbtList();
		for (var body : orbitingBodies) {
			NbtCompound orbNbt = new NbtCompound();
			body.writeToNbt(orbNbt);
			orbitingList.add(orbNbt);
		}
		nbt.put("orbitingBodies", orbitingList);
	}

	public void readFromNbt(NbtCompound nbt) {
		centerX = nbt.getDouble("centerX");
		centerY = nbt.getDouble("centerY");
		radius = nbt.getDouble("radius");
		orbitDistance = nbt.getDouble("orbitDistance");
		orbitSpeed = nbt.getDouble("orbitSpeed");
		orbitAngle = nbt.getDouble("orbitAngle");
		orbit = new GalaxyDim(nbt.getCompound("orbit"));
		if (nbt.getKeys().contains("body"))
			body = new GalaxyDim(nbt.getCompound("body"));

		NbtList orbitingList = nbt.getList("orbitingBodies", NbtElement.COMPOUND_TYPE);
		orbitingBodies = new CelestialBody[orbitingList.size()];
		for (int i = 0; i < orbitingList.size(); i++) {
			NbtCompound bodyNbt = orbitingList.getCompound(i);
			CelestialBody nBody = new CelestialBody();
			nBody.readFromNbt(bodyNbt);
			orbitingBodies[i] = nBody;
		}
	}
}
