package net.snakefangox.hyperstellar.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.client.render.ShipNameplateBER;
import net.snakefangox.hyperstellar.client.render.ShipRenderer;
import net.snakefangox.hyperstellar.dimensions.SpaceSkyProperties;
import net.snakefangox.hyperstellar.mixin.AccessSkyProperties;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.screens.BackupGeneratorScreen;
import net.snakefangox.hyperstellar.screens.FusionGeneratorScreen;
import net.snakefangox.hyperstellar.screens.GalacticMapScreen;
import net.snakefangox.hyperstellar.screens.ShipNameplateScreen;
import net.snakefangox.hyperstellar.screens.ShipyardScreen;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class HyperstellarClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Screens
		ScreenRegistry.<ShipyardScreen, CottonInventoryScreen<ShipyardScreen>>
				register(HScreens.SHIPYARD_SCREEN, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));
		ScreenRegistry.<ShipNameplateScreen, CottonInventoryScreen<ShipNameplateScreen>>
				register(HScreens.SHIP_NAMEPLATE_SCREEN, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));
		ScreenRegistry.<BackupGeneratorScreen, CottonInventoryScreen<BackupGeneratorScreen>>
				register(HScreens.BACKUP_GENERATOR_SCREEN, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));
		ScreenRegistry.<FusionGeneratorScreen, CottonInventoryScreen<FusionGeneratorScreen>>
				register(HScreens.FUSION_GENERATOR_SCREEN, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));
		ScreenRegistry.<GalacticMapScreen, CottonInventoryScreen<GalacticMapScreen>>
				register(HScreens.GALACTIC_MAP, (h, i, t) -> new CottonInventoryScreen<>(h, i.player, t));

		// Transparent Blocks
		BlockRenderLayerMap.INSTANCE.putBlock(HBlocks.SHIPYARD_TRUSS, RenderLayer.getCutout());

		// BER's
		BlockEntityRendererRegistry.INSTANCE.register(HEntities.SHIP_NAMEPLATE, ctx -> new ShipNameplateBER());

		// Entities
		EntityRendererRegistry.INSTANCE.register(HEntities.SHIP, ShipRenderer::new);

		// Sky
		AccessSkyProperties.getBY_IDENTIFIER().put(new Identifier(Hyperstellar.MODID, "space"),
				new SpaceSkyProperties(false, true, 0.5f, "cubemap"));
		AccessSkyProperties.getBY_IDENTIFIER().put(new Identifier(Hyperstellar.MODID, "orbit"),
				new SpaceSkyProperties(true, true, 0.5f, "cubemap"));
	}
}
