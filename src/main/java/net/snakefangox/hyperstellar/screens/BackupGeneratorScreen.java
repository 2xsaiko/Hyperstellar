package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.snakefangox.hyperstellar.register.HScreens;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Formatting;

public class BackupGeneratorScreen extends SyncedGuiDescription {

	public BackupGeneratorScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.BACKUP_GENERATOR_SCREEN, syncId, playerInventory, null, getBlockPropertyDelegate(ctx, 2));

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		WDynamicLabel isPowered = new WDynamicLabel(() -> {
			boolean powered = getPropertyDelegate().get(1) == 1;
			Formatting format = powered ? Formatting.DARK_GREEN : Formatting.RED;
			return format + I18n.translate(powered ? "hyperstellar.screen.generating" : "hyperstellar.screen.not_generating");
		});
		isPowered.setAlignment(HorizontalAlignment.CENTER);

		root.add(isPowered, 0, 1, 10, 1);

		WDynamicLabel timeRemaining = new WDynamicLabel(() -> {
			int powerTime = getPropertyDelegate().get(0) / 20;
			boolean isReady = powerTime > 1;
			return I18n.translate(isReady ? "hyperstellar.screen.time_remaining" : "hyperstellar.screen.recharging", Math.abs(powerTime));
		});
		timeRemaining.setAlignment(HorizontalAlignment.CENTER);

		root.add(timeRemaining, 0, 3, 10, 1);

		root.validate(this);
	}
}
