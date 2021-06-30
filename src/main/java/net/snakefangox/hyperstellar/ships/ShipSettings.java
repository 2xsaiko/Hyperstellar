package net.snakefangox.hyperstellar.ships;

import net.snakefangox.worldshell.entity.WorldShellSettings;
import net.snakefangox.worldshell.transfer.RotationSolver;

import java.util.Random;

public class ShipSettings {
	public static final WorldShellSettings SETTINGS = new WorldShellSettings.Builder(true, false)
			.setConflictSolver((world, pos, shellState, existingState) -> {
				world.breakBlock(pos, true);
				return existingState;
			}).setRotationSolver(RotationSolver.CARDINAL).build();

	private static final Random RAND = new Random();

	public static String getRandomShipName() {
		return NAMES[RAND.nextInt(NAMES.length)];
	}

	public static final String[] NAMES = new String[]{
			"Phoenix",
			"Gibraltar",
			"Agememnon",
			"Pontiac",
			"The Spectator",
			"ISS Challenger",
			"BC Gauntlet",
			"CS Invader",
			"BC Beluga",
			"BS The Messenger",
			"Cain",
			"Titan",
			"The Condor",
			"Katherina",
			"Andromeda",
			"HWSS Storm",
			"BS Rhapsody",
			"SSE Thebes",
			"SSE Observer",
			"ISS Flavia",
			"Black Sparrow",
			"Nebuchadnezzar",
			"Dauntless",
			"Javelin",
			"Carnage",
			"SSE Badger",
			"SSE Avenger",
			"ISS Javelin",
			"SS The Condor",
			"HWSS Annihilator",
			"Arcadian",
			"Eagle",
			"Angelica",
			"Burninator",
			"Templar",
			"STS Blue Whale",
			"LWSS The Gladiator",
			"STS Constantine",
			"HWSS Courage",
			"HWSS Firebrand",
			"Reliant",
			"Dispatcher",
			"Rhapsody",
			"Amazon",
			"Arcadian",
			"STS Katherina",
			"BS Katherina",
			"HMS The Titan",
			"HMS Legacy",
			"SC Typhoon",
	};
}
