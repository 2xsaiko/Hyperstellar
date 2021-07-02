package net.snakefangox.hyperstellar.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;

public class GalacticMapBER implements BlockEntityRenderer<ShipNameplateBE> {
	@Override
	public void render(ShipNameplateBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

	}

	@Override
	public boolean rendersOutsideBoundingBox(ShipNameplateBE blockEntity) {
		return true;
	}
}
