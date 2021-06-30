package net.snakefangox.hyperstellar.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.snakefangox.hyperstellar.ships.ShipEntity;
import net.snakefangox.worldshell.client.WorldShellEntityRenderer;

public class ShipRenderer extends WorldShellEntityRenderer<ShipEntity> {
	public ShipRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}
}
