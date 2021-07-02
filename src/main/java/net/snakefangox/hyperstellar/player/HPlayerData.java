package net.snakefangox.hyperstellar.player;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;

public class HPlayerData implements HPlayerDataComp {

	private final PlayerEntity playerEntity;
	private final byte[] skills = new byte[PlayerSkills.SKILL_COUNT];
	private int[] knownSectors = new int[0];

	public HPlayerData(PlayerEntity playerEntity) {
		this.playerEntity = playerEntity;
	}

	@Override
	public PlayerEntity getPlayer() {
		return playerEntity;
	}

	@Override
	public byte[] getPlayerSkills() {
		return skills;
	}

	@Override
	public int[] getKnownSectorIndexes() {
		return knownSectors;
	}

	@Override
	public void learnSector(int sectorIndex) {
		if (Arrays.stream(knownSectors).anyMatch(i -> i == sectorIndex)) return;
		int[] newSecs = Arrays.copyOf(knownSectors, knownSectors.length + 1);
		newSecs[newSecs.length - 1] = sectorIndex;
		knownSectors = newSecs;
	}
}
