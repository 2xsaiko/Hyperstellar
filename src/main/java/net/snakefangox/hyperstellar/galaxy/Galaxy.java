package net.snakefangox.hyperstellar.galaxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Galaxy implements HGalaxyComp {

	public static final int GALAXY_SIZE = 15;

	private final MinecraftServer server;
	private final Sector[] sectors = new Sector[GALAXY_SIZE * GALAXY_SIZE];
	private final BiMap<RegistryKey<World>, SectorPos> sectorKeyToPos = HashBiMap.create();
	private final Map<RegistryKey<World>, CelestialBody> keyToCelestialBody = new HashMap<>();
	private long galaxySeed;

	public Galaxy(Scoreboard scoreboard, @Nullable MinecraftServer server) {
		this.server = server;
		if (server != null) {
			var saveProp = server.getSaveProperties();
			if (saveProp != null) {
				galaxySeed = saveProp.getGeneratorOptions().getSeed();
			}
		}
	}

	@Override
	public void serverTick() {
		getAllExistingSectors().forEach(s -> s.tickSector(server.getTickTime()));
	}

	@Nullable
	@Override
	public Sector getSector(SectorPos pos) {
		return sectors[pos.getIndex()];
	}

	@Override
	public void setSector(SectorPos pos, Sector sector) {
		sectors[pos.getIndex()] = sector;
		sectorKeyToPos.put(sector.getSectorSpace().getWorldKey(), pos);
	}

	@Override
	public void generateSector(SectorPos pos) {
		if (server == null) return;
		Sector sector = getSector(pos);
		if (sector == null) {
			sectors[pos.getIndex()] = sector = new Sector(pos, this::registerCelestialBody);
		}

		sector.generateIfEmpty(server, galaxySeed * pos.hashCode(), null, this);
	}

	@Override
	public void generateDefaultSectors() {
		int centre = (GALAXY_SIZE / 2);
		SectorPos pos = new SectorPos(centre, centre);
		Sector sector = getSector(pos);
		if (sector == null) {
			sectors[pos.getIndex()] = sector = new Sector(pos, this::registerCelestialBody);
		}

		sector.generateIfEmpty(server, galaxySeed * pos.hashCode(),
				RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecraft", "overworld")), this);
	}

	public boolean sectorDimKeyExists(RegistryKey<World> name) {
		return sectorKeyToPos.containsKey(name);
	}

	public void registerCelestialBody(CelestialBody body) {
		keyToCelestialBody.put(body.getOrbit().getWorldKey(), body);
		var bodyKey = body.getBodyKey();
		if (bodyKey != null) {
			keyToCelestialBody.put(bodyKey, body);
		}
	}

	@Override
	public Stream<Sector> getAllExistingSectors() {
		return Arrays.stream(sectors).filter(Objects::nonNull);
	}

	@Override
	public void loadWorlds(MinecraftServer server) {
		getAllExistingSectors().forEach(sector -> sector.loadWorld(server));
	}

	@Override
	public boolean isSpaceDim(World dim) {
		var key = dim.getRegistryKey();
		return sectorKeyToPos.containsKey(key) ||
				(keyToCelestialBody.containsKey(key) && keyToCelestialBody.get(key).getOrbit().getWorldKey().equals(key));
	}

	@Override
	@Nullable
	public RegistryKey<World> getTransferDim(World dim, boolean isUp, double x, double y) {
		var key = dim.getRegistryKey();
		if (!isUp && sectorKeyToPos.containsKey(key)) {
			Sector sector = getSector(sectorKeyToPos.get(key));
			return sector.getOrbitOrSelfAtPos(x, y);
		} else if (keyToCelestialBody.containsKey(key)) {
			return keyToCelestialBody.get(key).getTransferKey(key, isUp);
		}
		return null;
	}

	@Override
	public void onLoad() {
		sectorKeyToPos.clear();
		keyToCelestialBody.clear();
	}

	@Override
	public long getGalaxySeed() {
		return galaxySeed;
	}

	@Override
	public void setGalaxySeed(long galaxySeed) {
		this.galaxySeed = galaxySeed;
	}
}
