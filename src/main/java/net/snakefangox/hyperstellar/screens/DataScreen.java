package net.snakefangox.hyperstellar.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface DataScreen {
	default void acceptServer(World world, PlayerEntity player, NbtCompound nbt){}
	default void acceptClient(World world, PlayerEntity player, NbtCompound nbt){}
}
