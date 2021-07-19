package net.snakefangox.hyperstellar.client.render;

import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GalacticMapBER implements BlockEntityRenderer<ShipNameplateBE> {
	@Override
	public void render(ShipNameplateBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

	}

	@Override
	public boolean rendersOutsideBoundingBox(ShipNameplateBE blockEntity) {
		return true;
	}
}
