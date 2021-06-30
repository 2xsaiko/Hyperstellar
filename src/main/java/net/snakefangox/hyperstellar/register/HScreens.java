package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.screens.ShipNameplateScreen;
import net.snakefangox.hyperstellar.screens.ShipyardScreen;

public class HScreens {
	public static ScreenHandlerType<ShipyardScreen> SHIPYARD_SCREEN;
	public static ScreenHandlerType<ShipNameplateScreen> SHIP_NAMEPLATE_SCREEN;

	public static void registerScreens() {
		SHIPYARD_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "shipyard"), (syncId, inventory) -> new ShipyardScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
		SHIP_NAMEPLATE_SCREEN = ScreenHandlerRegistry.registerSimple(new Identifier(Hyperstellar.MODID, "ship_nameplate"), (syncId, inventory) -> new ShipNameplateScreen(syncId, inventory, ScreenHandlerContext.EMPTY));
	}
}
