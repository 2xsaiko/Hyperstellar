package net.snakefangox.hyperstellar.blocks.entities;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.snakefangox.hyperstellar.register.HBlocks;
import net.snakefangox.hyperstellar.register.HEntities;
import net.snakefangox.hyperstellar.register.HItems;
import net.snakefangox.hyperstellar.screens.FusionGeneratorScreen;
import net.snakefangox.hyperstellar.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FusionGeneratorBE extends AbstractGeneratorBE implements PropertyDelegateHolder, ImplementedInventory, SidedInventory {
	public static final Text TITLE = new TranslatableText("hyperstellar.container.fusion_generator");
	public static final int POWER_TICKS = 20 * 60 * 60;
	public static final int BASE_POWER_LEVEL = 5;
	public static final int CHECK_INTERVAL = 20 * 10;
	public static final int MAX_SIDE_LEN = 20;

	private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private int powerTime = 0;
	private int powerLevel = 0;
	private long completedCheckTime = 0;
	private boolean checkCache = false;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> powerTime;
				case 1 -> isRedstonePowered() ? 1 : 0;
				case 2 -> powerLevel;
				case 3 -> checkCache ? 1 : 0;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0 -> powerTime = value;
			case 1 -> setRedstonePowered(value == 1);
			case 2 -> powerLevel = value;
			case 3 -> checkCache = value == 1;
			}
		}

		@Override
		public int size() {
			return 4;
		}
	};

	public FusionGeneratorBE(BlockPos pos, BlockState state) {
		super(HEntities.FUSION_REACTOR, pos, state);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return items;
	}

	@Override
	public void setRedstonePowered(boolean isPowered) {
		super.setRedstonePowered(isPowered && (world.isClient() || getOrCheckCompleted()) && powerTime > 0);
	}

	@Override
	protected int getPowerRadius() {
		return 10;
	}

	@Override
	protected int tryGenerate() {
		if (isRedstonePowered()) {
			if (powerTime > 0) {
				--powerTime;
				return powerLevel;
			}
		}

		if (powerTime == 0) {
			ItemStack fuel = items.get(0);
			ItemStack out = items.get(1);
			boolean outEmpty = (out.isOf(HItems.EMPTY_FUSION_CELL) && out.getCount() < out.getMaxCount()) || out.isEmpty();
			if (fuel.isOf(HItems.FILLED_FUSION_CELL) && fuel.getCount() > 0 && outEmpty) {
				removeStack(0, 1);
				setStack(1, new ItemStack(HItems.EMPTY_FUSION_CELL, out.getCount() + 1));
				powerTime = POWER_TICKS;
				return powerLevel;
			}
		}

		return 0;
	}

	private boolean getOrCheckCompleted() {
		if (world.getTime() - completedCheckTime > CHECK_INTERVAL) {
			completedCheckTime = world.getTime();
			checkCache = checkStructure();
		}
		return checkCache;
	}

	private boolean checkStructure() {
		BlockPos.Mutable pointer = new BlockPos.Mutable();
		BlockPos.Mutable pointerWall = new BlockPos.Mutable();
		Direction point = getCachedState().get(Properties.HORIZONTAL_FACING).getOpposite();
		BlockPos end = pos.offset(point);
		pointer.set(end);
		point = point.rotateYCounterclockwise();
		int i = 0;
		boolean hasTurned = false;
		while (i < MAX_SIDE_LEN * 4) {
			if (pointer.equals(end) && i != 0) {
				powerLevel = i + BASE_POWER_LEVEL;
				return true;
			}

			++i;
			BlockState state = world.getBlockState(pointer);
			if (state.isOf(HBlocks.FUSION_REACTOR_CHANNEL)) {
				hasTurned = false;
				boolean valid = true;
				valid = valid && checkValidWall(pointerWall.set(pointer.getX(), pointer.getY() + 1, pointer.getZ()));
				valid = valid && checkValidWall(pointerWall.set(pointer.getX(), pointer.getY() - 1, pointer.getZ()));
				var lWall = point.rotateYCounterclockwise();
				valid = valid && checkValidWall(pointerWall.set(pointer.getX() + lWall.getOffsetX(), pointer.getY(), pointer.getZ() + lWall.getOffsetZ()));
				var rWall = point.rotateYCounterclockwise();
				valid = valid && checkValidWall(pointerWall.set(pointer.getX() + rWall.getOffsetX(), pointer.getY(), pointer.getZ() + rWall.getOffsetZ()));

				if (!valid) return false;

				pointer.set(pointer.getX() + point.getOffsetX(), pointer.getY(), pointer.getZ() + point.getOffsetZ());
			} else if (!hasTurned) {
				hasTurned = true;
				pointer.set(pointer.getX() - point.getOffsetX(), pointer.getY(), pointer.getZ() - point.getOffsetZ());
				point = point.rotateYClockwise();
				pointer.set(pointer.getX() + point.getOffsetX(), pointer.getY(), pointer.getZ() + point.getOffsetZ());
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean checkValidWall(BlockPos.Mutable set) {
		BlockState state = world.getBlockState(set);
		return state.isOf(HBlocks.FUSION_REACTOR_COIL) || state.isOf(HBlocks.FUSION_REACTOR_CONTROLLER) || state.isOf(HBlocks.FUSION_REACTOR_CHANNEL);
	}

	@Override
	public Text getDisplayName() {
		return TITLE;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		powerTime = nbt.getInt("powerTime");
		powerLevel = nbt.getInt("powerLevel");
		Inventories.readNbt(nbt, items);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("powerTime", powerTime);
		nbt.putInt("powerLevel", powerLevel);
		Inventories.writeNbt(nbt, items);
		return super.writeNbt(nbt);
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new FusionGeneratorScreen(syncId, inv, ScreenHandlerContext.create(world, pos));
	}

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return new int[] {0};
		} else if (side == Direction.DOWN) {
			return new int[] {1};
		}
		return new int[0];
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return dir == Direction.UP || slot == 0;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return dir == Direction.DOWN || slot == 1;
	}
}
