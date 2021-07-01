package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.screens.DataScreen;
import net.snakefangox.hyperstellar.ships.ShipEntity;

public class HClientPackets {
	public static final Identifier SH_DATA_PACKET = HServerPackets.SH_DATA_PACKET;

	public static void registerClientPackets() {
		ClientPlayNetworking.registerGlobalReceiver(SH_DATA_PACKET, (client, handler, buf, responseSender) -> {
			NbtCompound nbt = buf.readNbt();

			client.execute(() -> {
				PlayerEntity player = MinecraftClient.getInstance().player;
				if (player.currentScreenHandler instanceof DataScreen) {
					((DataScreen)player.currentScreenHandler).acceptClient(player.world, player, nbt);
				}
			});
		});
	}
}
