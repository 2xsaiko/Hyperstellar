package net.snakefangox.hyperstellar.galaxy;

public record SectorPos(int x, int y) {

	public static SectorPos fromIndex(int index) {
		return new SectorPos(index % Galaxy.GALAXY_SIZE, index / Galaxy.GALAXY_SIZE);
	}

	public int getIndex() {
		return x + Galaxy.GALAXY_SIZE * y;
	}

	public String toCoordString() {
		return "(" + x + ", " + y + ")";
	}
}
