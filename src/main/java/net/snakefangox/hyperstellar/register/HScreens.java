package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.blocks.entities.FusionGeneratorBE;
import net.snakefangox.hyperstellar.screens.BackupGeneratorScreen;
import net.snakefangox.hyperstellar.screens.FusionGeneratorScreen;
import net.snakefangox.hyperstellar.screens.ShipNameplateScreen;
import net.snakefangox.hyperstellar.screens.ShipyardScreen;

public class HScreens {
	public static ScreenHandlerType<ShipyardScreen> SHIPYARD_SCREEN;
	public static ScreenHandlerType<ShipNameplateScreen> SHIP_NAMEPLATE_SCREEN;
	public static ScreenHandlerType<BackupGeneratorScreen> BACKUP_GENERATOR_SCREEN;
	public static ScreenHandlerType<FusionGeneratorScreen> FUSION_GENERATOR_SCREEN;

	public static void registerScreens() {
		SHIPYARD_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "shipyard"), (syncId, inventory) -> new ShipyardScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
		SHIP_NAMEPLATE_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "ship_nameplate"), (syncId, inventory) -> new ShipNameplateScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
		BACKUP_GENERATOR_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "backup_generator"), (syncId, inventory) -> new BackupGeneratorScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
		FUSION_GENERATOR_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "fusion_generator"), (syncId, inventory) -> new FusionGeneratorScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
	}
}
