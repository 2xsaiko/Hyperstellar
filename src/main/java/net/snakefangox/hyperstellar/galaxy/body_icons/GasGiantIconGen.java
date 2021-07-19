package net.snakefangox.hyperstellar.galaxy.body_icons;

import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.snakefangox.hyperstellar.util.TextureGenerator;

public class GasGiantIconGen extends TextureGenerator {
	private final float hue;

	public GasGiantIconGen(long seed, int size, float hue) {
		super(seed, size, 8f);
		this.hue = hue;
	}

	@Override
	protected int determineColor(int x, int y, float height) {
		var aHeight = Math.abs(height);
		float shadow = Math.max(x, y) > size - 3 ? 0.7f : 1;
		shadow *= Math.max(x, y) > size - 2 ? 0.7f : 1;
		return new Color.HSL(hue + (height / 4f), 1 - aHeight, 0.4f * shadow).toRgb();
	}
}
