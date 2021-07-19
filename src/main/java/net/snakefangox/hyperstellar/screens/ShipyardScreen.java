package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.snakefangox.hyperstellar.blocks.entities.ShipyardControllerBE;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.screens.widgets.WDynamicText;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.util.TriState;

public class ShipyardScreen extends SyncedGuiDescription implements DataScreen {
	private static final Identifier TERMINAL_TEX = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/panel_dark.png");
	private static final TranslatableText BOOTING = new TranslatableText("hyperstellar.screen.shipyard_boot");
	private static final TranslatableText READY = new TranslatableText("hyperstellar.screen.shipyard_ready");
	private static final TranslatableText NOT_READY = new TranslatableText("hyperstellar.screen.shipyard_not_ready");
	private static final TranslatableText BUILDING = new TranslatableText("hyperstellar.screen.shipyard_building");
	private static final TranslatableText BUILT = new TranslatableText("hyperstellar.screen.shipyard_build_done");
	private static final TranslatableText FAILED = new TranslatableText("hyperstellar.screen.shipyard_build_fail");

	private final BlockPos pos;
	private LiteralText report = null;

	public ShipyardScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.SHIPYARD_SCREEN, syncId, playerInventory, null, getBlockPropertyDelegate(ctx, 1));
		pos = ctx.get((world1, blockPos) -> blockPos, BlockPos.ORIGIN);

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		WGridPanel bg = new WGridPanel();
		bg.setInsets(new Insets(5));
		bg.setBackgroundPainter(BackgroundPainter.createNinePatch(TERMINAL_TEX));
		root.add(bg, 0, 1, 16, 3);

		WDynamicText terminal = new WDynamicText(getTerminalText(), 0x0CE53A);
		terminal.setSize(12 * 18, 20 * 18);
		terminal.setTextSupplier(this::getTerminalText);

		WScrollPanel screen = new WScrollPanel(terminal);
		screen.setScrollingHorizontally(TriState.FALSE);
		bg.add(screen, 0, 0, 15, 3);

		WButton checkShipyard = new WButton(new TranslatableText("hyperstellar.screen.check_shipyard"));
		checkShipyard.setOnClick(() -> sendButtonPress(0));
		root.add(checkShipyard, 0, 6, 5, 1);

		WButton scanShip = new WButton(new TranslatableText("hyperstellar.screen.scan_ship"));
		scanShip.setOnClick(() -> sendButtonPress(1));
		root.add(scanShip, 6, 6, 4, 1);

		WButton buildShip = new WButton(new TranslatableText("hyperstellar.screen.build_ship"));
		buildShip.setOnClick(() -> sendButtonPress(2));
		root.add(buildShip, 11, 6, 5, 1);

		root.validate(this);
	}

	private void sendButtonPress(int id) {
		report = null;
		MinecraftClient.getInstance().interactionManager.clickButton(syncId, id);
	}


	private BaseText getTerminalText() {
		if (report != null) {
			return report;
		}

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
				((ShipyardControllerBE) entity).getCommand(player, id);
			}
		}
		return super.onButtonClick(player, id);
	}

	@Override
	public void acceptClient(World world, PlayerEntity player, NbtCompound nbt) {
		if (nbt.getString("report") != null) {
			report = new LiteralText(nbt.getString("report"));
		}
	}
}
