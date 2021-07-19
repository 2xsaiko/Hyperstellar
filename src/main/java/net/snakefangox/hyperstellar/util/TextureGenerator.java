package net.snakefangox.hyperstellar.util;

import net.minecraft.util.math.Vec2f;
import net.snakefangox.hyperstellar.galaxy.body_icons.ContinentalPlanetIconGen;
import net.snakefangox.hyperstellar.galaxy.body_icons.GasGiantIconGen;
import net.snakefangox.hyperstellar.galaxy.body_icons.StarIconGen;
import net.snakefangox.hyperstellar.world_gen.OpenSimplexNoise;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public abstract class TextureGenerator {

	protected final OpenSimplexNoise[] heightNoise;
	protected final float[][] heightmap;
	protected final int[][] icon;
	private final float scale;
	protected final int size;
	protected final Vec2f center;

	public TextureGenerator(long seed, int size, float scale) {
		this.heightNoise = new OpenSimplexNoise[]{new OpenSimplexNoise(seed), new OpenSimplexNoise(seed * 5),
				new OpenSimplexNoise(seed * 7), new OpenSimplexNoise(seed * 9)};
		this.size = size;
		heightmap = new float[size][size];
		icon = new int[size][size];
		this.scale = scale;
		float center = size / 2f;
		this.center = new Vec2f(center, center);
	}

	public void generate() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int i = 0; i < heightNoise.length; i++) {
					var rI = (float) 1 + i;
					heightmap[x][y] += heightNoise[i].sample(x / (scale / rI), y / (scale / rI)) * (1 / rI);
				}
			}
		}

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				var height = heightmap[x][y];
				icon[x][y] = determineColor(x, y, height);
			}
		}
	}

	protected abstract int determineColor(int x, int y, float height);

	protected int minDistFromEdge(int x, int y) {
		return Math.min(minDistFromXEdge(x), minDistFromYEdge(y));
	}

	protected int minDistFromXEdge(int x) {
		return Math.min(x, size - (x + 1));
	}

	protected int minDistFromYEdge(int y) {
		return Math.min(y, size - (y + 1));
	}

	protected float distFromCenter(int x, int y) {
		return (float) Math.hypot(x - center.x, y - center.y);
	}

	public static void main(String[] args) throws Throwable {
		BufferedImage toSave = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		var rand = new Random();
		var icon = new GasGiantIconGen(rand.nextLong(), 32, 0.3f);
		icon.generate();

		for (int x = 0; x < toSave.getWidth(); x++) {
			for (int y = 0; y < toSave.getHeight(); y++) {
				toSave.setRGB(x, y, icon.icon[x][y]);
			}
		}

		File outputfile = new File("icon.png");
		ImageIO.write(toSave, "png", outputfile);
	}
}
