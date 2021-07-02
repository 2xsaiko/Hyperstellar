package net.snakefangox.hyperstellar.blocks.entities;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.screens.BackupGeneratorScreen;
import org.jetbrains.annotations.Nullable;

public class BackupGeneratorBE extends AbstractGeneratorBE implements PropertyDelegateHolder {
	public static final Text TITLE = new TranslatableText("hyperstellar.container.backup_generator");
	public static final int RECHARGE_TICKS = 20 * 60 * 15;
	public static final int POWER_TICKS = 20 * 60 * 5;
	public static final int POWER_AMOUNT = 10;

	private int powerTime = -RECHARGE_TICKS;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			if (index == 0)
				return powerTime;
			return isRedstonePowered() ? 1 : 0;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				powerTime = value;
			} else {
				setRedstonePowered(value == 1);
			}
		}

		@Override
		public int size() {
			return 2;
		}
	};

	public BackupGeneratorBE(BlockPos pos, BlockState state) {
		super(HEntities.BACKUP_GENERATOR, pos, state);
	}

	@Override
	public void setRedstonePowered(boolean isPowered) {
		super.setRedstonePowered(isPowered && powerTime > 0);
	}

	@Override
	protected int getPowerRadius() {
		return 3;
	}

	@Override
	protected int tryGenerate() {
		if (isRedstonePowered()) {
			if (powerTime > 0) {
				--powerTime;
				return POWER_AMOUNT;
			} else {
				if (powerTime == 0) setRedstonePowered(false);
				powerTime = -RECHARGE_TICKS;
			}
		} else {
			if (powerTime > 0 && powerTime < POWER_TICKS) {
				++powerTime;
			} else {
				powerTime = POWER_TICKS;
			}
		}
		return 0;
	}

	@Override
	public Text getDisplayName() {
		return TITLE;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		powerTime = nbt.getInt("powerTime");
		super.readNbt(nbt);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("powerTime", powerTime);
		return super.writeNbt(nbt);
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new BackupGeneratorScreen(syncId, inv, ScreenHandlerContext.create(world, pos));
	}

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
	}
}
