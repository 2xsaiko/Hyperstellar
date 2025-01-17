package net.snakefangox.hyperstellar.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(SkyProperties.class)
@Environment(EnvType.CLIENT)
public interface AccessSkyProperties {
	@Accessor
	static Object2ObjectMap<Identifier, SkyProperties> getBY_IDENTIFIER() {
		throw new AssertionError();
	}
}
