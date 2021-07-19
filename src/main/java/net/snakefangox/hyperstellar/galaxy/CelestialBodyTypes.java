package net.snakefangox.hyperstellar.galaxy;

import net.snakefangox.hyperstellar.Hyperstellar;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public enum CelestialBodyTypes {

	STAR("star"), CONTINENTAL_PLANET("continental_planet"), MOON("moon"), ICE_PLANET("ice_planet"), GAS_GIANT("gas_giant");

	private static final Identifier SPRITE_SHEET = new Identifier(Hyperstellar.MODID, "textures/environment/celestial_body.png");
	private final String name;
	private final TranslatableText langKey;
	CelestialBodyTypes(String name) {
		this.name = name;
		this.langKey = new TranslatableText(Hyperstellar.MODID + ".body_type." + name);
	}

	public String getName() {
		return name;
	}

	public TranslatableText getLangKey() {
		return langKey;
	}

	public int getIconIndex() {
		return ordinal();
	}

	public float getStartUv() {
		return ordinal() / (float) values().length;
	}

	public float getEndUv() {
		return (ordinal() + 1) / (float) values().length;
	}
}
