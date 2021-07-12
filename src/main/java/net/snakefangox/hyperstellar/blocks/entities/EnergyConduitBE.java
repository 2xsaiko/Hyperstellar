package net.snakefangox.hyperstellar.blocks.entities;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.snakefangox.hyperstellar.blocks.EnergyConduit;
import net.snakefangox.hyperstellar.register.HEntities;

public class EnergyConduitBE extends BlockEntity implements BlockEntityClientSerializable {

	EnergyConduit.ConduitConnection[] connections = new EnergyConduit.ConduitConnection[4];

	public EnergyConduitBE(BlockPos pos, BlockState state) {
		super(HEntities.ENERGY_CONDUIT, pos, state);
	}

	public void setConnect(Direction dir, EnergyConduit.ConduitConnection connectType) {
		connections[dir.getHorizontal()] = connectType;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		fromClientTag(nbt);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		toClientTag(nbt);
		return super.writeNbt(nbt);
	}

	@Override
	public void fromClientTag(NbtCompound nbt) {
		var connects = nbt.getByteArray("connections");
		for (int i = 0; i < connects.length; i++)
			connections[i] = EnergyConduit.ConduitConnection.values()[connects[i]];
	}

	@Override
	public NbtCompound toClientTag(NbtCompound nbt) {
		NbtByteArray connects = new NbtByteArray(new byte[]{(byte) connections[0].ordinal(), (byte) connections[1].ordinal(),
				(byte) connections[2].ordinal(), (byte) connections[3].ordinal()});
		nbt.put("connections", connects);
		return nbt;
	}
}
