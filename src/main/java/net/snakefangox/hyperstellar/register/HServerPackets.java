package net.snakefangox.hyperstellar.register;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.snakefangox.hyperstellar.Hyperstellar;
import net.snakefangox.hyperstellar.screens.DataScreen;

public class HServerPackets {
	public static final Identifier SH_DATA_PACKET = new Identifier(Hyperstellar.MODID, "sh");

	public static void registerServerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(SH_DATA_PACKET, (server, player, handler, buf, responseSender) -> {
			NbtCompound nbt = buf.readNbt();

			server.execute(() -> {
				if (player.currentScreenHandler instanceof DataScreen) {
					((DataScreen)player.currentScreenHandler).acceptServer(player.world, player, nbt);
				}
			});
		});
	}
}
