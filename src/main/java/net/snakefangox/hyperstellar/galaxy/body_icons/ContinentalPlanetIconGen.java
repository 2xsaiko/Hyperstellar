package net.snakefangox.hyperstellar.galaxy.body_icons;

import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.snakefangox.hyperstellar.util.TextureGenerator;

public class ContinentalPlanetIconGen extends TextureGenerator {
	private final float landHue;
	private final float oceanHue;

	public ContinentalPlanetIconGen(long seed, int size, float landHue, float oceanHue) {
		super(seed, size, 5.5f);
		this.landHue = landHue;
		this.oceanHue = oceanHue;
	}

	@Override
	protected int determineColor(int x, int y, float height) {
		height /= 2;
		float shadow = Math.max(x, y) > size - 3 ? 0.7f : 1;
		shadow *= Math.max(x, y) > size - 2 ? 0.7f : 1;

		if (height < 0) {
			var sixthHeight = (height / -6);
			return new Color.HSL(oceanHue, 0.8f, (0.5f - sixthHeight) * shadow).toRgb();
		} else {
			return new Color.HSL(landHue, 0.5f, 1 * (height + 0.1f) * shadow).toRgb();
		}
	}
}
