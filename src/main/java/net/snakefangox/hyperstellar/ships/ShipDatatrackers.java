package net.snakefangox.hyperstellar.ships;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShipDatatrackers {
	public static final TrackedDataHandler<Map<UUID, Integer>> PASSENGER_MAP = new TrackedDataHandler<>() {
		@Override
		public void write(PacketByteBuf buf, Map<UUID, Integer> value) {
			buf.writeMap(value, PacketByteBuf::writeUuid, PacketByteBuf::writeInt);
		}

		@Override
		public Map<UUID, Integer> read(PacketByteBuf buf) {
			return buf.readMap(PacketByteBuf::readUuid, PacketByteBuf::readInt);
		}

		@Override
		public Map<UUID, Integer> copy(Map<UUID, Integer> value) {
			return new HashMap<>(value);
		}
	};

	public static final TrackedDataHandler<ShipData> SHIP_DATA = new TrackedDataHandler<ShipData>() {
		@Override
		public void write(PacketByteBuf buf, ShipData value) {
			buf.writeNbt(value.toNbt());
		}

		@Override
		public ShipData read(PacketByteBuf buf) {
			return new ShipData(buf.readNbt());
		}

		@Override
		public ShipData copy(ShipData value) {
			return new ShipData(value.toNbt());
		}
	};
}
