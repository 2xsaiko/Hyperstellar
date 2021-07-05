package net.snakefangox.hyperstellar.galaxy;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Galaxy implements HGalaxyComp {

	public static final int GALAXY_SIZE = 15;

	private final MinecraftServer server;
	private long galaxySeed;
	private final Sector[] sectors = new Sector[GALAXY_SIZE * GALAXY_SIZE];

	public Galaxy(Scoreboard scoreboard, @Nullable MinecraftServer server) {
		this.server = server;
		if (server != null) {
			var saveProp = server.getSaveProperties();
			if (saveProp != null)
				galaxySeed = saveProp.getGeneratorOptions().getSeed();
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
	}

	@Override
	public void generateSector(SectorPos pos) {
		if (server == null) return;
		Sector sector = getSector(pos);
		if (sector == null)
			sectors[pos.getIndex()] = sector = new Sector(pos);

		sector.generateIfEmpty(server, galaxySeed * pos.hashCode(), null);
	}

	@Override
	public void generateOverworldSector() {
		int centre = (GALAXY_SIZE / 2);
		SectorPos pos = new SectorPos(centre, centre);
		Sector sector = getSector(pos);
		if (sector == null)
			sectors[pos.getIndex()] = sector = new Sector(pos);

		sector.generateIfEmpty(server, galaxySeed * pos.hashCode(), RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecraft", "overworld")));
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
	public boolean isSpaceDim(DimensionType dimension) {
		if (server == null) return false;
		DimensionType sectorSpaceType = server.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(Sector.SECTOR_TYPE);
		DimensionType orbitSpaceType = server.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(CelestialBody.ORBIT_TYPE);

		return dimension.equals(sectorSpaceType) || dimension.equals(orbitSpaceType);
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
