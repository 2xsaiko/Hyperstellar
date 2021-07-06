package net.snakefangox.hyperstellar.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import net.snakefangox.hyperstellar.galaxy.GalaxyLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements Nameable, EntityLike, CommandOutput {
	@Shadow
	public World world;

	@Inject(method = "tick", at = @At("HEAD"))
	protected void tickInVoid(CallbackInfo ci) {
		if (!world.isClient())
			GalaxyLogic.checkAndHandleEntityTransfer((Entity) (Object) this);
	}
}
