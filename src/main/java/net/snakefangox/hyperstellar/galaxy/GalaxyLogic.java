package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.snakefangox.hyperstellar.register.HComponents;
import net.snakefangox.worldshell.world.CreateWorldsEvent;

public class GalaxyLogic {
	public static final double TRANSFER_HEIGHT = 612;

	public static void registerGalaxyLogic() {
		CreateWorldsEvent.EVENT.register(server -> HComponents.GALAXY.get(server.getScoreboard()).loadWorlds(server));
		ServerLifecycleEvents.SERVER_STARTED.register(GalaxyLogic::addOverworldSector);
	}

	private static void addOverworldSector(MinecraftServer server) {
		var galaxy = HComponents.GALAXY.get(server.getScoreboard());
		galaxy.generateOverworldSector();
	}
}
