package net.snakefangox.hyperstellar.dimensions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public interface CustomSky {
	void renderSky(MinecraftClient client, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Runnable runnable, VertexBuffer starsBuffer);
}
