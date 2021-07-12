package net.snakefangox.hyperstellar.galaxy.body_icons;

import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.snakefangox.hyperstellar.util.TextureGenerator;

public class StarIconGen extends TextureGenerator {
	private final float hue;
	private final float lum;

	public StarIconGen(long seed, int size, float hue, float lum) {
		super(seed, size, 3);
		this.hue = hue;
		this.lum = lum;
	}

	@Override
	protected int determineColor(int x, int y, float height) {
		height = height / 9f;
		int color;

		var minDist = minDistFromEdge(x, y);
		var dist = 255 - (minDist < (size / 2.9) ? Math.min(255, 255 * (distFromCenter(x, y) / size * 2)) : 0);

		if (dist < 255) {
			color = new Color.HSL(hue, 0.4f, lum / 3f).toRgb();
		} else {
			var rim = minDist < (size / 2.6) ? 0.7f : (1 - height);
			rim *= minDist < (size / 2.3) ? 0.8f : 1;
			color = new Color.HSL(hue * (1 - (height * 0.1f)), 1 * rim, lum * rim).toRgb();
		}

		color &= 0x00_FFFFFF;
		color = ((int) dist << 24) | color;
		return color;
	}
}
