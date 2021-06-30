package net.snakefangox.hyperstellar.blocks.entities;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.hyperstellar.register.HEntities;

public class ShipNameplateBE extends BlockEntity implements BlockEntityClientSerializable {

	private String name = "The Nameless One";

	public ShipNameplateBE(BlockPos pos, BlockState state) {
		super(HEntities.SHIP_NAMEPLATE, pos, state);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		sync();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		name = nbt.getString("name");
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putString("name", name);
		return super.writeNbt(nbt);
	}

	@Override
	public void fromClientTag(NbtCompound tag) {
		name = tag.getString("name");
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag) {
		tag.putString("name", name);
		return tag;
	}
}
