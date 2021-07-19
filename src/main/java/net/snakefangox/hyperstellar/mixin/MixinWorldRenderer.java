package net.snakefangox.hyperstellar.mixin;

import net.snakefangox.hyperstellar.dimensions.CustomSky;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class MixinWorldRenderer implements SynchronousResourceReloader, AutoCloseable {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Nullable
	private VertexBuffer starsBuffer;

	@Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V",
			at = @At("HEAD"))
	void renderSky(MatrixStack matrices, Matrix4f matrix4f, float f, Runnable runnable, CallbackInfo ci) {
		var skyProp = client.world.getSkyProperties();
		if (skyProp instanceof CustomSky) {
			((CustomSky) skyProp).renderSky(client, matrices, matrix4f, f, runnable, starsBuffer);
		}
	}
}
