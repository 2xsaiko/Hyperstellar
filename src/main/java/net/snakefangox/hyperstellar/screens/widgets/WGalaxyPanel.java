package net.snakefangox.hyperstellar.screens.widgets;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.Scissors;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.snakefangox.hyperstellar.galaxy.Galaxy;
import net.snakefangox.hyperstellar.galaxy.HGalaxyComp;
import net.snakefangox.hyperstellar.galaxy.Sector;
import net.snakefangox.hyperstellar.galaxy.SectorPos;
import net.snakefangox.hyperstellar.player.HPlayerDataComp;

import java.util.function.BiConsumer;

public class WGalaxyPanel extends WPanel {

	private static final double CLICK_DIST = 5;

	private final BiConsumer<Sector, SectorPos> flipToSector;
	private int grid, offX, offY, clickX = -1, clickY = -1;

	public WGalaxyPanel(HGalaxyComp galaxy, HPlayerDataComp playerData, BiConsumer<Sector, SectorPos> flipToSector) {
		this.flipToSector = flipToSector;
		grid = 28;

		for (int x = 0; x < Galaxy.GALAXY_SIZE; x++) {
			for (int y = 0; y < Galaxy.GALAXY_SIZE; y++) {
				var sPos = new SectorPos(x, y);
				var sector = galaxy.getSector(sPos);
				if (sector == null) {
					addNullSector(sPos);
				} else if (playerData.knowsSector(sector)) {
					addKnownSector(sector);
				} else {
					addUnknownSector(sector);
				}
			}
		}

		setBackgroundPainter(BackgroundPainter.createColorful(0xFF000000));
	}

	private void addNullSector(SectorPos pos) {
		add(new WSectorSelect(flipToSector, pos), pos.x(), pos.y());
	}

	private void addUnknownSector(Sector sector) {
		addNullSector(sector.getPos());
	}

	private void addKnownSector(Sector sector) {
		SectorPos pos = sector.getPos();
		add(new WSectorSelect(flipToSector, sector), pos.x(), pos.y());
	}

	private void add(WSectorSelect wSectorSelect, int x, int y) {
		children.add(wSectorSelect);
		wSectorSelect.setParent(this);
		wSectorSelect.setSize(grid, grid);
		wSectorSelect.setLocation(x * grid, y * grid);
	}

	public void setScroll(int oX, int oY) {
		offX = MathHelper.clamp(oX, width - (Galaxy.GALAXY_SIZE * grid), 0);
		offY = MathHelper.clamp(oY, height - (Galaxy.GALAXY_SIZE * grid), 0);
	}

	@Override
	public WWidget hit(int x, int y) {
		return this;
	}

	@Override
	public InputResult onMouseDown(int x, int y, int button) {
		clickX = x;
		clickY = y;
		return InputResult.PROCESSED;
	}

	@Override
	public InputResult onMouseUp(int x, int y, int button) {
		if (clickX > -1 && clickY > -1 && Math.hypot(x - clickX, y - clickY) < CLICK_DIST) {
			super.hit(x - offX, y - offY).onClick(x, y, button);
		}
		return InputResult.PROCESSED;
	}

	@Override
	public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
		setScroll(offX + (int) deltaX, offY + (int) deltaY);
		return InputResult.PROCESSED;
	}

	@Override
	public InputResult onMouseScroll(int x, int y, double amount) {
		if (amount > 0) {
			zoomIn();
		} else {
			zoomOut();
		}
		return InputResult.PROCESSED;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (getBackgroundPainter() != null) getBackgroundPainter().paintBackground(matrices, x, y, this);

		Scissors.push(x, y, width, height);
		for (WWidget child : children) {
			child.paint(matrices, x + child.getX() + offX, y + child.getY() + offY,
					mouseX - (child.getX() + offX), mouseY - (child.getY() + offY));
		}
		Scissors.pop();
	}

	public void zoomIn() {
		zoom(4);
	}

	public void zoomOut() {
		zoom(-4);
	}

	public void zoomToNormal() {
		zoom(28 - grid);
	}

	private void zoom(int amount) {
		int oldGrid = grid;
		grid += amount;
		grid = MathHelper.clamp(grid, 16, 44);
		children.forEach(c -> {
			c.setLocation((c.getX() / oldGrid) * grid, (c.getY() / oldGrid) * grid);
			c.setSize(grid, grid);
		});
		setScroll(offX, offY);
	}

	@Override
	protected void expandToFit(WWidget w) {
	}
}
