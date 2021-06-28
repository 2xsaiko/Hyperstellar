package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.hyperstellar.blocks.entities.ShipyardControllerBE;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.register.HServerPackets;
import net.snakefangox.hyperstellar.screens.widgets.WDynamicText;

public class ShipyardScreen extends SyncedGuiDescription {
	private static final Identifier TERMINAL_TEX = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/panel_dark.png");
	private static final TranslatableText BOOTING = new TranslatableText("hyperstellar.screen.shipyard_boot");
	private static final TranslatableText READY = new TranslatableText("hyperstellar.screen.shipyard_ready");
	private static final TranslatableText NOT_READY = new TranslatableText("hyperstellar.screen.shipyard_not_ready");
	private static final TranslatableText BUILDING = new TranslatableText("hyperstellar.screen.shipyard_building");
	private static final TranslatableText BUILT = new TranslatableText("hyperstellar.screen.shipyard_build_done");
	private static final TranslatableText FAILED = new TranslatableText("hyperstellar.screen.shipyard_build_fail");

	private final BlockPos pos;

	public ShipyardScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.SHIPYARD_SCREEN, syncId, playerInventory, null, getBlockPropertyDelegate(ctx, 1));
		pos = ctx.get((world1, blockPos) -> blockPos, BlockPos.ORIGIN);

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		WGridPanel screen = new WGridPanel();
		screen.setInsets(new Insets(10));
		screen.setBackgroundPainter(BackgroundPainter.createNinePatch(TERMINAL_TEX));
		root.add(screen, 0, 1, 16, 3);

		WDynamicText terminal = new WDynamicText(getTerminalText(), 0x0CE53A);
		terminal.setTextSupplier(this::getTerminalText);
		screen.add(terminal, 0, 0, 12, 3);

		WButton checkShipyard = new WButton(new TranslatableText("hyperstellar.screen.check_shipyard"));
		checkShipyard.setOnClick(() -> MinecraftClient.getInstance().interactionManager.clickButton(syncId, 0));
		root.add(checkShipyard, 0, 6, 5, 1);

		WButton scanShip = new WButton(new TranslatableText("hyperstellar.screen.scan_ship"));
		scanShip.setOnClick(() -> MinecraftClient.getInstance().interactionManager.clickButton(syncId, 1));
		root.add(scanShip, 6, 6, 4, 1);

		WButton buildShip = new WButton(new TranslatableText("hyperstellar.screen.build_ship"));
		buildShip.setOnClick(() -> MinecraftClient.getInstance().interactionManager.clickButton(syncId, 2));
		root.add(buildShip, 11, 6, 5, 1);

		root.validate(this);
	}


	private TranslatableText getTerminalText() {
		ShipyardControllerBE.State state = ShipyardControllerBE.State.values()[getPropertyDelegate().get(0)];
		return switch (state) {
			case BOOTED -> BOOTING;
			case READY -> READY;
			case NOTREADY -> NOT_READY;
			case BUILDING -> BUILDING;
			case BUILT -> BUILT;
			case FAILED -> FAILED;
		};
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (world.isChunkLoaded(pos)) {
			BlockEntity entity = world.getBlockEntity(pos);
			if (entity instanceof ShipyardControllerBE) {
				((ShipyardControllerBE) entity).getCommand(id);
			}
		}
		return super.onButtonClick(player, id);
	}
}
