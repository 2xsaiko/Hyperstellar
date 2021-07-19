package net.snakefangox.hyperstellar.screens.widgets;

import java.util.function.BiConsumer;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.snakefangox.hyperstellar.galaxy.Sector;
import net.snakefangox.hyperstellar.galaxy.SectorPos;

import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class WSectorSelect extends WWidget {

	public static final int LINE_STROKE = 1;
	private final BiConsumer<Sector, SectorPos> flipToSector;
	private final SectorPos pos;
	private final Sector sector;

	public WSectorSelect(BiConsumer<Sector, SectorPos> flipToSector, SectorPos pos) {
		this.flipToSector = flipToSector;
		this.pos = pos;
		sector = null;
	}

	public WSectorSelect(BiConsumer<Sector, SectorPos> flipToSector, Sector sector) {
		this.flipToSector = flipToSector;
		pos = sector.getPos();
		this.sector = sector;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public InputResult onClick(int x, int y, int button) {
		flipToSector.accept(sector, pos);
		return super.onClick(x, y, button);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean hovered = isWithinBounds(mouseX, mouseY) && parent.isWithinBounds(mouseX, mouseY);
		int color = hovered ? 0xFFFFFFFF : 0x22FFFFFF;

		ScreenDrawing.coloredRect(matrices, x, y, width - LINE_STROKE, LINE_STROKE, color);
		ScreenDrawing.coloredRect(matrices, x, y + LINE_STROKE, LINE_STROKE, height - (LINE_STROKE * 2), color);
		ScreenDrawing.coloredRect(matrices, x + (width - LINE_STROKE), y, LINE_STROKE, height, color);
		ScreenDrawing.coloredRect(matrices, x, y + (height - LINE_STROKE), width - LINE_STROKE, LINE_STROKE, color);

		if (width > 36) {
			ScreenDrawing.drawString(matrices, pos.toCoordString(), HorizontalAlignment.CENTER, x, y + (height - 10), width, 0x22FFFFFF);
		}
	}
}
