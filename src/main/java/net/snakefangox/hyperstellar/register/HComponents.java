package net.snakefangox.hyperstellar.register;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.galaxy.Galaxy;
import net.snakefangox.hyperstellar.galaxy.HGalaxyComp;
import net.snakefangox.hyperstellar.player.HPlayerData;
import net.snakefangox.hyperstellar.player.HPlayerDataComp;

public class HComponents {

	public static final ComponentKey<HPlayerDataComp> PLAYER_DATA = ComponentRegistry.getOrCreate(new Identifier(Hyperstellar.MODID, "player_data"), HPlayerDataComp.class);
	public static final ComponentKey<HGalaxyComp> GALAXY = ComponentRegistry.getOrCreate(new Identifier(Hyperstellar.MODID, "galaxy"), HGalaxyComp.class);

	public static void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(PLAYER_DATA, HPlayerData::new, RespawnCopyStrategy.ALWAYS_COPY);
	}

	public static void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(GALAXY, Galaxy::new);
	}
}
