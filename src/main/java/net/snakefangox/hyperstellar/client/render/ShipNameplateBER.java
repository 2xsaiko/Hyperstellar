package net.snakefangox.hyperstellar.client.render;

import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class ShipNameplateBER implements BlockEntityRenderer<ShipNameplateBE> {
	@Override
	public void render(ShipNameplateBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		int width = textRenderer.getWidth(entity.getName());
		matrices.push();
		Direction dir = entity.getCachedState().get(Properties.HORIZONTAL_FACING);
		matrices.scale(0.1f, -0.1f, 0.1f);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-dir.asRotation()));
		if (dir.getDirection() == Direction.AxisDirection.POSITIVE) {
			matrices.translate(0, 0, 5);
		} else {
			matrices.translate(0, 0, -5);
		}
		textRenderer.draw(matrices, entity.getName(), -width / 2f, -textRenderer.fontHeight, 0xFFFFFF);
		matrices.pop();
	}

	@Override
	public boolean rendersOutsideBoundingBox(ShipNameplateBE blockEntity) {
		return true;
	}

	@Override
	public int getRenderDistance() {
		return 128;
	}
}
