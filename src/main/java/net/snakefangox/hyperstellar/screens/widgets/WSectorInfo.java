package net.snakefangox.hyperstellar.screens.widgets;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.snakefangox.hyperstellar.galaxy.Sector;
import net.snakefangox.hyperstellar.galaxy.SectorPos;

public class WSectorInfo extends WWidget {

	private Sector sector;
	private SectorPos pos;

	@Override
	@Environment(EnvType.CLIENT)
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		ScreenDrawing.coloredRect(matrices, x, y, width, height, 0xFF000000);
		ScreenDrawing.drawString(matrices, pos.toCoordString(), HorizontalAlignment.CENTER, x, y + 12, width, 0x22FFFFFF);

		if (sector == null) {
			ScreenDrawing.drawString(matrices, I18n.translate("hyperstellar.screen.sector_unknown"),
					HorizontalAlignment.CENTER, x, y + (height / 2), width, 0xFFFF2222);
			return;
		}

		sector.drawSector(matrices, x + (width / 2d), y + (height / 2d));
	}

	@Override
	public boolean canResize() {
		return true;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector, SectorPos pos) {
		this.sector = sector;
		this.pos = pos;
	}
}
