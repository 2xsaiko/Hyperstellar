package net.snakefangox.hyperstellar.util;

import java.util.Locale;
import java.util.Random;

public class WordMarkovChain {

	private static final byte ASCII_OFFSET = 96;
	private static final byte CHAR_COUNT = 28;

	private final float[][] charProbabilities = new float[CHAR_COUNT][CHAR_COUNT];

	public WordMarkovChain(String corpus) {
		var words = corpus.toLowerCase(Locale.ROOT).split(" ");

		for (var word : words) train("``" + word + "{");
		normalize();
	}

	public String generate(Random random, int maxCount, float alpha) {
		StringBuilder generated = new StringBuilder();
		generated.append("``");

		for (int i = 2; i < maxCount + 2; i++) {
			int idx = 0;

			if (alpha < 1 && random.nextFloat() > alpha) {
				idx = random.nextInt(CHAR_COUNT - 2) + 1;
			} else {
				int lastChIdx = generated.charAt(i - 1) - ASCII_OFFSET;
				double weight = random.nextDouble();

				while (weight > 0)
					weight -= charProbabilities[lastChIdx][idx++];

				idx -= 1;
			}

			char nChar = (char) (idx + ASCII_OFFSET);
			if (nChar == '{') {
				break;
			} else {
				generated.append(nChar);
			}
		}

		return generated.substring(2);
	}

	private void train(String word) {
		for (int i = 2; i < word.length(); i++) {
			char ch = word.charAt(i);
			char lastCh = word.charAt(i - 1);
			charProbabilities[lastCh - ASCII_OFFSET][ch - ASCII_OFFSET] += 1;
		}
	}

	private void normalize() {
		for (int i = 0; i < CHAR_COUNT; i++) {
			double mag = 0;

			for (int k = 0; k < CHAR_COUNT; k++)
				mag += Math.pow(charProbabilities[i][k], 2);

			mag = 1d / Math.sqrt(mag);

			if (Double.isFinite(mag)) {
				for (int k = 0; k < CHAR_COUNT; k++)
					charProbabilities[i][k] *= mag;
			} else {
				mag = 1d / (CHAR_COUNT - 1);
				for (int k = 1; k < CHAR_COUNT; k++)
					charProbabilities[i][k] += mag;
			}
		}
	}
}
