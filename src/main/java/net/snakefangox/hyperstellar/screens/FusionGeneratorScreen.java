package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Formatting;
import net.snakefangox.hyperstellar.register.HScreens;

public class FusionGeneratorScreen extends SyncedGuiDescription {

	public FusionGeneratorScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.FUSION_GENERATOR_SCREEN, syncId, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx, 4));

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		WDynamicLabel isValid = new WDynamicLabel(() -> {
			boolean valid = getPropertyDelegate().get(3) == 1;
			Formatting format = valid ? Formatting.DARK_GREEN : Formatting.RED;
			return format + I18n.translate(valid ? "hyperstellar.screen.structure_valid" : "hyperstellar.screen.structure_invalid");
		});
		root.add(isValid, 0, 1, 5, 1);

		WDynamicLabel isPowered = new WDynamicLabel(() -> {
			boolean powered = getPropertyDelegate().get(1) == 1;
			Formatting format = powered ? Formatting.DARK_GREEN : Formatting.RED;
			return format + I18n.translate(powered ? "hyperstellar.screen.generating" : "hyperstellar.screen.not_generating");
		});
		root.add(isPowered, 0, 2, 5, 1);

		WDynamicLabel timeRemaining = new WDynamicLabel(() -> {
			int timeLeft = getPropertyDelegate().get(0) / 20;
			return I18n.translate("hyperstellar.screen.time_remaining", timeLeft);
		});
		root.add(timeRemaining, 0, 3, 8, 1);

		WDynamicLabel powerProduced = new WDynamicLabel(() -> {
			int powerLevel = getPropertyDelegate().get(0) > 0 ? getPropertyDelegate().get(2) : 0;
			return I18n.translate("hyperstellar.screen.power_generated", powerLevel);
		});
		root.add(powerProduced, 0, 5, 8, 1);

		WItemSlot inSlot = WItemSlot.of(blockInventory, 0);
		root.add(inSlot, 8, 3);

		WItemSlot outSlot = WItemSlot.of(blockInventory, 1);
		outSlot.setInsertingAllowed(false);
		root.add(outSlot, 8, 5);

		root.add(createPlayerInventoryPanel(), 0, 7);

		root.validate(this);
	}
}
