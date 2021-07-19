package net.snakefangox.hyperstellar.ships;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public class ShipModifier {

	private final String name;
	private final Modifier[] modifiers;

	public ShipModifier(String name, Modifier... modifiers) {
		this.name = name;
		this.modifiers = modifiers;
	}

	public String getName() {
		return name;
	}

	public Modifier getModifier(int index) {
		return modifiers[index];
	}

	public Modifier[] getModifiers() {
		return modifiers;
	}

	public enum Operation implements StringIdentifiable {
		MULT("multiply", (a, b) -> a * b), ADD("add", Double::sum),
		BOOST("boost", Math::max), LIMIT("limit", Math::min);

		private final String name;
		private final BiFunction<Double, Double, Double> op;

		Operation(String name, BiFunction<Double, Double, Double> op) {
			this.name = name;
			this.op = op;
		}

		public double apply(double value, double mod) {
			return op.apply(value, mod);
		}

		public Formatting getTextColour(double mod, boolean applySelf, boolean goodStat) {
			boolean nice = applySelf == goodStat;
			boolean increaseH = apply(1000000.0, mod) >= 1000000.0;
			boolean increaseL = apply(1.0, mod) > 1.0;
			boolean increase = increaseH && increaseL;
			return nice == increase ? Formatting.GREEN : Formatting.RED;
		}

		@Override
		public String asString() {
			return null;
		}
	}

	public static record Modifier(String property, Operation op, double mod) implements Comparable<Modifier> {
		@Override
		public int compareTo(@NotNull ShipModifier.Modifier o) {
			int c = o.op.ordinal() - op.ordinal();
			return c == 0 ? Double.compare(o.mod, mod) : c;
		}
	}
}
