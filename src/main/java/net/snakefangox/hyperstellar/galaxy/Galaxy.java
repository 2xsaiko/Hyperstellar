package net.snakefangox.hyperstellar.galaxy;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Galaxy implements HGalaxyComp {

	public static final int GALAXY_SIZE = 15;

	private long galaxySeed;
	private final Sector[] sectors = new Sector[GALAXY_SIZE * GALAXY_SIZE];

	public Galaxy(Scoreboard scoreboard, @Nullable MinecraftServer server) {
		if (server != null) {
			var saveProp = server.getSaveProperties();
			if (saveProp != null)
				galaxySeed = saveProp.getGeneratorOptions().getSeed();
		}
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
		Sector sector = getSector(pos);
		if (sector == null)
			sectors[pos.getIndex()] = sector = new Sector(pos);

		sector.generateIfEmpty(galaxySeed * pos.hashCode());
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
	public long getGalaxySeed() {
		return galaxySeed;
	}

	@Override
	public void setGalaxySeed(long galaxySeed) {
		this.galaxySeed = galaxySeed;
	}
}
