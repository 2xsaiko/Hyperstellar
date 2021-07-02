package net.snakefangox.hyperstellar.blocks.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.snakefangox.hyperstellar.power.PowerReceiver;
import net.snakefangox.hyperstellar.power.PowerUtil;

import java.util.Collections;
import java.util.List;

public abstract class AbstractGeneratorBE extends BlockEntity implements NamedScreenHandlerFactory {

	private List<BlockPos> demandOffsets = Collections.emptyList();
	private boolean powered;

	public AbstractGeneratorBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected abstract int getPowerRadius();

	protected abstract int tryGenerate();

	protected void provide(int amount) {
		int i = 0;
		while (amount > 0 && i < getDemandOffsets().size()) {
			var oPos = getDemandOffsets().get(i).add(pos);
			var state = world.getBlockState(oPos);
			var block = state.getBlock();
			if (block instanceof PowerReceiver) {
				int needed = ((PowerReceiver) block).maxPowerNeeded(world, state, oPos);
				if (needed > 0) {
					((PowerReceiver) block).providePower(world, state, oPos, Math.min(needed, amount));
					amount -= needed;
				}
			}
		}
	}

	protected List<BlockPos> getDemandOffsets() {
		return demandOffsets;
	}

	public void setRedstonePowered(boolean isPowered) {
		powered = isPowered;
		if (!world.isClient())
			world.setBlockState(pos, getCachedState().with(Properties.POWERED, isPowered));
	}

	public boolean isRedstonePowered() {
		return powered;
	}

	protected void getPoweredBlocks() {
		demandOffsets = PowerUtil.getNearbyPowerDemands(world, pos, getPowerRadius());
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putBoolean("powered", powered);
		return super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		powered = nbt.getBoolean("powered");
		super.readNbt(nbt);
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t) {
		AbstractGeneratorBE be = (AbstractGeneratorBE) t;
		int amount = be.tryGenerate();
		be.provide(amount);
		if (world.getTime() % 20 == 0) be.getPoweredBlocks();
	}
}
