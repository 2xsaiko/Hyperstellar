package net.snakefangox.hyperstellar.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import net.snakefangox.hyperstellar.galaxy.GalaxyLogic;
import net.snakefangox.hyperstellar.galaxy.SectorPos;
import net.snakefangox.hyperstellar.register.HComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(Entity.class)
public abstract class MixinEntity implements Nameable, EntityLike, CommandOutput {
	@Shadow
	public World world;

	@Inject(method = "tick", at = @At("HEAD"))
	protected void tickInVoid(CallbackInfo ci) {
		if (world.isClient()) return;

		var galaxy = HComponents.GALAXY.get(world.getServer().getScoreboard());
		boolean isSpace = galaxy.isSpaceDim(world.getDimension());
		var entity = (Entity) (Object) this;

		if (isSpace && entity.getY() < -64) {
			AccessTeleportCommand.invokeTeleport(world.getServer().getCommandSource(), entity, world.getServer().getOverworld(),
					entity.getX(), entity.getY() + GalaxyLogic.TRANSFER_HEIGHT, entity.getZ(),
					EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), entity.getYaw(), entity.getPitch(), null);
		} else if (entity.getY() > GalaxyLogic.TRANSFER_HEIGHT) {
			var key = galaxy.getSector(new SectorPos(15 / 2, 15 / 2)).getSystemCenter().getOrbit().getWorldKey();
			AccessTeleportCommand.invokeTeleport(world.getServer().getCommandSource(), entity, world.getServer().getWorld(key),
					entity.getX(), entity.getY() - GalaxyLogic.TRANSFER_HEIGHT, entity.getZ(),
					EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), entity.getYaw(), entity.getPitch(), null);
		}
	}
}
