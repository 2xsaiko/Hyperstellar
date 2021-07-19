package net.snakefangox.hyperstellar.client.render;

import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.worldshell.client.WorldShellEntityRenderer;

import net.minecraft.client.render.entity.EntityRendererFactory;

public class ShipRenderer extends WorldShellEntityRenderer<ShipEntity> {
	public ShipRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}
}
