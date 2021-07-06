package net.snakefangox.hyperstellar.galaxy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.snakefangox.hyperstellar.mixin.AccessTeleportCommand;
import net.snakefangox.hyperstellar.register.HComponents;
import net.snakefangox.worldshell.world.CreateWorldsEvent;

import java.util.EnumSet;

public class GalaxyLogic {
	public static final double SPACE_HEIGHT = 100;

	public static void registerGalaxyLogic() {
		CreateWorldsEvent.EVENT.register(server -> HComponents.GALAXY.get(server.getScoreboard()).loadWorlds(server));
		ServerLifecycleEvents.SERVER_STARTED.register(GalaxyLogic::generateDefaultSectors);
	}

	private static void generateDefaultSectors(MinecraftServer server) {
		var galaxy = HComponents.GALAXY.get(server.getScoreboard());
		galaxy.generateDefaultSectors();
	}

	public static void checkAndHandleEntityTransfer(Entity entity) {
		var world = entity.world;
		double tNegY = world.getBottomY() - 64;
		if (entity.getY() >= tNegY && entity.getY() < world.getTopY() + SPACE_HEIGHT) return;
		var galaxy = HComponents.GALAXY.get(world.getServer().getScoreboard());
		var isDown = entity.getY() < tNegY;
		var toKey = galaxy.getTransferDim(world, !isDown, entity.getX(), entity.getY());

		if (toKey != null) {
			var toWorld = world.getServer().getWorld(toKey);
			double newY;
			if (isDown) {
				newY = toWorld.getTopY() + SPACE_HEIGHT - (entity.getHeight() * 2);
			} else {
				newY = toWorld.getBottomY();
			}

			AccessTeleportCommand.invokeTeleport(world.getServer().getCommandSource(), entity, toWorld,
					entity.getX(), newY, entity.getZ(), EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class),
					entity.getYaw(), entity.getPitch(), null);
		}
	}
}
