package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.snakefangox.hyperstellar.blocks.entities.ShipNameplateBE;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.register.HServerPackets;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class ShipNameplateScreen extends SyncedGuiDescription implements DataScreen {
	private final BlockPos pos;

	public ShipNameplateScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.SHIP_NAMEPLATE_SCREEN, syncId, playerInventory, null, getBlockPropertyDelegate(ctx, 1));
		pos = ctx.get((world1, blockPos) -> blockPos, BlockPos.ORIGIN);

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		WTextField field = new WTextField();
		root.add(field, 1, 1, 8, 1);

		WButton nameShip = new WButton(new TranslatableText("hyperstellar.screen.name_ship"));
		nameShip.setOnClick(() -> {
			NbtCompound nbt = new NbtCompound();
			nbt.putString("name", field.getText());
			ClientPlayNetworking.send(HServerPackets.SH_DATA_PACKET, PacketByteBufs.create().writeNbt(nbt));
		});
		root.add(nameShip, 0, 4, 10, 1);

		root.validate(this);
	}

	@Override
	public void acceptServer(World world, PlayerEntity player, NbtCompound nbt) {
		if (world.isChunkLoaded(pos) && nbt.getString("name") != null) {
			BlockEntity entity = world.getBlockEntity(pos);
			if (entity instanceof ShipNameplateBE) {
				((ShipNameplateBE) entity).setName(nbt.getString("name"));
			}
		}
	}
}
