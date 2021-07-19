package net.snakefangox.hyperstellar.galaxy;

import java.util.stream.Stream;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface HGalaxyComp extends Component, ServerTickingComponent, AutoSyncedComponent {

	long getGalaxySeed();

	void setGalaxySeed(long galaxySeed);

	@Nullable
	Sector getSector(SectorPos pos);

	void setSector(SectorPos pos, Sector sector);

	void generateSector(SectorPos pos);

	void generateDefaultSectors();

	void registerCelestialBody(CelestialBody body);

	Stream<Sector> getAllExistingSectors();

	void loadWorlds(MinecraftServer server);

	boolean isSpaceDim(World dim);

	@Nullable RegistryKey<World> getTransferDim(World dim, boolean isUp, double x, double y);

	@Override
	default void writeToNbt(NbtCompound nbt) {
		nbt.putLong("seed", getGalaxySeed());
		NbtList sectorList = new NbtList();
		getAllExistingSectors().forEach(sector -> {
			NbtCompound secNbt = new NbtCompound();
			sector.writeToNbt(secNbt);
			sectorList.add(sectorList.size(), secNbt);
		});
		nbt.put("sectors", sectorList);
	}

	@Override
	default void readFromNbt(NbtCompound nbt) {
		onLoad();
		setGalaxySeed(nbt.getLong("seed"));
		NbtList sectorList = (NbtList) nbt.get("sectors");
		for (int i = 0; i < sectorList.size(); i++) {
			NbtCompound nbtSector = sectorList.getCompound(i);
			SectorPos pos = SectorPos.fromIndex(nbtSector.getInt("pos"));
			Sector sector = new Sector(pos, this::registerCelestialBody);
			sector.readFromNbt(nbtSector);
			setSector(pos, sector);
		}
	}

	void onLoad();
}
