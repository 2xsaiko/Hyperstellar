package net.snakefangox.hyperstellar.screens;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.snakefangox.hyperstellar.galaxy.HGalaxyComp;
import net.snakefangox.hyperstellar.player.HPlayerDataComp;
import net.snakefangox.hyperstellar.register.HComponents;
import net.snakefangox.hyperstellar.register.HScreens;
import net.snakefangox.hyperstellar.screens.widgets.WGalaxyPanel;
import net.snakefangox.hyperstellar.screens.widgets.WSectorInfo;

public class GalacticMapScreen extends SyncedGuiDescription {
	public GalacticMapScreen(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
		super(HScreens.GALACTIC_MAP, syncId, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx));
		PlayerEntity player = playerInventory.player;

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);
		setTitleVisible(false);

		HPlayerDataComp playerData = HComponents.PLAYER_DATA.get(player);
		HGalaxyComp galaxy = HComponents.GALAXY.get(player.world.getScoreboard());

		WCardPanel cardPanel = new WCardPanel();

		WGridPanel infoPanel = new WGridPanel();
		WSectorInfo sectorInfo = new WSectorInfo();
		infoPanel.add(sectorInfo, 0, 0, 13, 13);

		WButton backButton = new WButton(new LiteralText("<"));
		backButton.setOnClick(() -> cardPanel.setSelectedIndex(1));
		infoPanel.add(backButton, 0, 0);

		WGalaxyPanel galaxyPanel = new WGalaxyPanel(galaxy, playerData, (sector, pos) -> {
			sectorInfo.setSector(sector, pos);
			cardPanel.setSelectedIndex(0);
		});
		cardPanel.add(infoPanel, 13, 13);
		cardPanel.add(galaxyPanel, 13, 13);

		cardPanel.setSelectedIndex(1);
		root.add(cardPanel, 0, 0, 13, 13);

		addZoomButtons(root, galaxyPanel);

		WItemSlot hotbar = WItemSlot.of(playerInventory, 0, 1, 9);
		root.add(hotbar, 13, 4);

		root.validate(this);
	}

	private void addZoomButtons(WGridPanel root, WGalaxyPanel galaxyPanel) {
		WButton zoomIn = new WButton(new LiteralText("+"));
		zoomIn.setOnClick(galaxyPanel::zoomIn);
		root.add(zoomIn, 13, 0);

		WButton zoomToNormal = new WButton(new LiteralText("1:1"));
		zoomToNormal.setOnClick(galaxyPanel::zoomToNormal);
		root.add(zoomToNormal, 13, 1);

		WButton zoomOut = new WButton(new LiteralText("-"));
		zoomOut.setOnClick(galaxyPanel::zoomOut);
		root.add(zoomOut, 13, 2);
	}
}
