package net.snakefangox.hyperstellar.dimensions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.snakefangox.hyperstellar.Hyperstellar;

@Environment(EnvType.CLIENT)
public class SpaceSkyProperties extends SkyProperties implements CustomSky {

	private static final float THIRD = (float) (1.0 / 3.0);
	private static final float TWO_THIRD = (float) (1.0 / 3.0) * 2f;
	private static final String SKYBOX_FOLDER = "textures/environment/";
	private static final String EXT = ".png";

	private final boolean isOrbit;
	private final boolean hasStars;
	private final float brightness;
	private final Identifier skybox;

	public SpaceSkyProperties(boolean isOrbit, boolean hasStars, float brightness, String skyBoxName) {
		super(Float.NaN, false, SkyType.NONE, true, false);
		this.isOrbit = isOrbit;
		this.hasStars = hasStars;
		this.brightness = brightness;
		skybox = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + EXT);
	}

	@Override
	public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
		return color.multiply(0.01);
	}

	@Override
	public boolean useThickFog(int camX, int camY) {
		return false;
	}

	@Override
	public float[] getFogColorOverride(float skyAngle, float tickDelta) {
		return null;
	}

	@Override
	public void renderSky(MinecraftClient client, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Runnable runnable, VertexBuffer starsBuffer) {
		float skyRot = client.world.getSkyAngleRadians(tickDelta);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShaderTexture(0, skybox);

		matrices.push();
		Matrix4f mat = matrices.peek().getModel();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		renderSkyPlanes(bufferBuilder, mat);
		tessellator.draw();
		matrices.pop();

		if (hasStars) {
			matrices.push();
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(skyRot));
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			BackgroundRenderer.method_23792();
			starsBuffer.setShader(matrices.peek().getModel(), matrix4f, GameRenderer.getPositionShader());
			runnable.run();
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private void renderSkyPlanes(BufferBuilder bufferBuilder, Matrix4f mat) {
		// Down
		bufferBuilder.vertex(mat, -100.0F, -100.0F, -100.0F).texture(0.25F, 1F).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, -100.0F, 100.0F).texture(0.25F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, -100.0F, 100.0F).texture(0.5F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, -100.0F, -100.0F).texture(0.5F, 1F).color(brightness, brightness, brightness, 1f).next();

		// Front
		bufferBuilder.vertex(mat, -100.0F, -100.0F, 100.0F).texture(0.25F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, 100.0F).texture(0.25F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, 100.0F, 100.0F).texture(0.5F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, -100.0F, 100.0F).texture(0.5F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();

		// Up
		bufferBuilder.vertex(mat, 100.0F, 100.0F, -100.0F).texture(0.5F, 0F).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, 100.0F, 100.0F).texture(0.5F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, 100.0F).texture(0.25F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, -100.0F).texture(0.25F, 0F).color(brightness, brightness, brightness, 1f).next();

		// Right
		bufferBuilder.vertex(mat, 100.0F, -100.0F, 100.0F).texture(0.5F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, 100.0F, 100.0F).texture(0.5F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, 100.0F, -100.0F).texture(0.75F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, -100.0F, -100.0F).texture(0.75F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();

		// Left
		bufferBuilder.vertex(mat, -100.0F, -100.0F, -100.0F).texture(0F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, -100.0F).texture(0F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, 100.0F).texture(0.25F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, -100.0F, 100.0F).texture(0.25F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();

		// Back
		bufferBuilder.vertex(mat, 100.0F, -100.0F, -100.0F).texture(0.75F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, 100.0F, 100.0F, -100.0F).texture(0.75F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, 100.0F, -100.0F).texture(1F, THIRD).color(brightness, brightness, brightness, 1f).next();
		bufferBuilder.vertex(mat, -100.0F, -100.0F, -100.0F).texture(1F, TWO_THIRD).color(brightness, brightness, brightness, 1f).next();
	}
}
