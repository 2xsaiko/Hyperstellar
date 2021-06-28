package net.snakefangox.hyperstellar.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.screens.ShipyardScreen;

@Environment(EnvType.CLIENT)
public class HyperstellarClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Screens
		ScreenRegistry.<ShipyardScreen, CottonInventoryScreen<ShipyardScreen>>
				register(HScreens.SHIPYARD_SCREEN, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));

		// Transparent Blocks
		BlockRenderLayerMap.INSTANCE.putBlock(HBlocks.SHIPYARD_TRUSS, RenderLayer.getCutout());
	}
}
