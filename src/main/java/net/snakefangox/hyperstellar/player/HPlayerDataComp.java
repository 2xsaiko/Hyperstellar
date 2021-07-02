package net.snakefangox.hyperstellar.player;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.snakefangox.hyperstellar.galaxy.Sector;
import net.snakefangox.hyperstellar.galaxy.SectorPos;
import net.snakefangox.hyperstellar.register.HComponents;

import java.util.function.Predicate;

public interface HPlayerDataComp extends Component, AutoSyncedComponent {

	PlayerEntity getPlayer();

	byte[] getPlayerSkills();

	default boolean hasSkill(int skillId) {
		return getPlayerSkills()[skillId] > 0;
	}

	default int getSkillLevel(int skillId) {
		return getPlayerSkills()[skillId];
	}

	default void unlockSkill(int skillId) {
		if (getPlayerSkills()[skillId] == 0) {
			getPlayerSkills()[skillId] = 1;
			HComponents.PLAYER_DATA.sync(getPlayer());
		}
	}

	default void levelSkill(int skillId) {
		if (getPlayerSkills()[skillId] != 0) {
			getPlayerSkills()[skillId] += 1;
			HComponents.PLAYER_DATA.sync(getPlayer());
		}
	}

	int[] getKnownSectorIndexes();

	void learnSector(int sectorIndex);

	default void learnSector(SectorPos sector) {
		learnSector(sector.getIndex());
	}

	default boolean knowsSector(Sector sector) {
		int sectorIndex = sector.getPos().getIndex();
		for (var s : getKnownSectorIndexes())
			if (s == sectorIndex) return true;
		return false;
	}

	@Override
	default void readFromNbt(NbtCompound nbt) {
		var skillArr = nbt.getByteArray("skills");
		for (int i = 0; i < skillArr.length; i++)
			getPlayerSkills()[i] = skillArr[i];
	}

	@Override
	default void writeToNbt(NbtCompound nbt) {
		nbt.putByteArray("skills", getPlayerSkills());
	}
}
