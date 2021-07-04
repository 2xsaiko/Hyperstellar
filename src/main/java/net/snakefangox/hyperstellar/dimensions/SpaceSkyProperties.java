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

	private static final String SKYBOX_FOLDER = "textures/environment/";
	private static final String EXT = ".png";

	private final boolean isOrbit;
	private final boolean hasStars;
	private final float brightness;
	private final Identifier skyBoxUp;
	private final Identifier skyBoxDown;
	private final Identifier skyBoxFoward;
	private final Identifier skyBoxLeft;
	private final Identifier skyBoxRight;
	private final Identifier skyBoxBack;

	public SpaceSkyProperties(boolean isOrbit, boolean hasStars, float brightness, String skyBoxName) {
		super(Float.NaN, true, SkyType.NONE, true, false);
		this.isOrbit = isOrbit;
		this.hasStars = hasStars;
		this.brightness = brightness;
		skyBoxUp = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_up" + EXT);
		skyBoxDown = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_down" + EXT);
		skyBoxFoward = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_front" + EXT);
		skyBoxLeft = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_left" + EXT);
		skyBoxRight = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_right" + EXT);
		skyBoxBack = new Identifier(Hyperstellar.MODID, SKYBOX_FOLDER + skyBoxName + "_back" + EXT);
	}

	@Override
	public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
		return color;
	}

	@Override
	public boolean useThickFog(int camX, int camY) {
		return false;
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

		for(int i = 0; i < 6; ++i) {
			matrices.push();

			switch (i) {
				case 1 -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				case 2 -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
				case 3 -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
				case 4 -> matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				case 5 -> matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
			}

			RenderSystem.setShaderTexture(0, switch (i) {
				case 0 -> skyBoxDown;
				case 1 -> skyBoxBack;
				case 2 -> skyBoxFoward;
				case 3 -> skyBoxUp;
				case 4 -> skyBoxLeft;
				case 5 -> skyBoxRight;
				default -> throw new IllegalStateException("Unexpected value: " + i);
			});

			Matrix4f oldMat = matrices.peek().getModel();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(oldMat, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(brightness, brightness, brightness, 1f).next();
			bufferBuilder.vertex(oldMat, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(brightness, brightness, brightness, 1f).next();
			bufferBuilder.vertex(oldMat, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(brightness, brightness, brightness, 1f).next();
			bufferBuilder.vertex(oldMat, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(brightness, brightness, brightness, 1f).next();
			tessellator.draw();

			matrices.pop();
		}

		matrices.push();
		if (isOrbit)
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(skyRot));
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		BackgroundRenderer.method_23792();
		starsBuffer.setShader(matrices.peek().getModel(), matrix4f, GameRenderer.getPositionShader());
		runnable.run();
		matrices.pop();

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
