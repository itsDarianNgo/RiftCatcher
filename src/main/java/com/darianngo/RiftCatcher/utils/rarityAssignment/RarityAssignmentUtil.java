package com.darianngo.RiftCatcher.utils.rarityAssignment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RarityAssignmentUtil {

	private static final String CSV_PATH = "C:/Users/daria/OneDrive/Desktop/RiftCatcher - Champion Stats.csv";
	private static final List<String> RARITIES = Arrays.asList("Common", "Rare", "Epic", "Legendary");
	private static final Map<String, Double> RARITY_DISTRIBUTION = new HashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4049275794212255054L;

		{
			put("Common", 0.4);
			put("Rare", 0.3);
			put("Epic", 0.20);
			put("Legendary", 0.1);
		}
	};

	public static void assignRarities() {
		List<String> lines = readCsv();
		List<String> updatedLines = new ArrayList<>();

		// Assuming first line is the header
		updatedLines.add(lines.get(2) + ",Rarity");

		// Skip the header for processing
		for (int i = 1; i < lines.size(); i++) {
			String rarity = getRandomRarity();
			updatedLines.add(lines.get(i) + "," + rarity);
		}

		writeCsv(updatedLines);
	}

	private static List<String> readCsv() {
		try {
			return Files.readAllLines(Paths.get(CSV_PATH), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read the CSV file.", e);
		}
	}

	private static void writeCsv(List<String> lines) {
		try {
			Files.write(Paths.get(CSV_PATH), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write to the CSV file.", e);
		}
	}

	private static String getRandomRarity() {
		double total = 0;
		for (double value : RARITY_DISTRIBUTION.values()) {
			total += value;
		}

		double randomValue = Math.random() * total;
		double cumulativeProbability = 0.0;

		for (Map.Entry<String, Double> entry : RARITY_DISTRIBUTION.entrySet()) {
			cumulativeProbability += entry.getValue();
			if (randomValue <= cumulativeProbability) {
				return entry.getKey();
			}
		}
		return "Common"; // Default rarity
	}
}
