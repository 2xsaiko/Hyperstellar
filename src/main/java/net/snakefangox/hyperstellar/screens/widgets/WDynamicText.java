package net.snakefangox.hyperstellar.screens.widgets;

import java.util.function.Supplier;

import io.github.cottonmc.cotton.gui.widget.WText;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class WDynamicText extends WText {
	private Supplier<Text> textSupplier;

	public WDynamicText(Text defaultText) {
		super(defaultText);
	}

	public WDynamicText(Text defaultText, int color) {
		super(defaultText, color);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (textSupplier != null) {
			Text newText = textSupplier.get();
			if (!newText.equals(text)) {
				setText(newText);
			}
		}
		super.paint(matrices, x, y, mouseX, mouseY);
	}

	public void setTextSupplier(Supplier<Text> textSupplier) {
		this.textSupplier = textSupplier;
	}
}
