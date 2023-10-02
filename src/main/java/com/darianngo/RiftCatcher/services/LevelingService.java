package com.darianngo.RiftCatcher.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class LevelingService {

	private static final double A = 50;
	private static final double B = 40;
	private static final double C = 100;
	private static final double D = 20;

	private Map<Integer, Double> xpCache = new HashMap<>(); // Cache to store XP requirements for levels

	private static final double LAMBDA = 0.2;
	private static final int MAX_LEVEL = 100;
	private static List<Double> cdf = new ArrayList<>();

	public double xpRequiredForLevel(int level) {
		// Check cache first
		if (xpCache.containsKey(level)) {
			return xpCache.get(level);
		}

		double xpRequired;
		if (1 <= level && level <= 30) {
			xpRequired = A * Math.pow(level, 2);
		} else if (31 <= level && level <= 70) {
			xpRequired = B * Math.pow(level, 2) + C * level;
		} else if (71 <= level && level <= 100) {
			xpRequired = D * Math.pow(level, 2.5);
		} else {
			throw new IllegalArgumentException("Level out of range: " + level);
		}

		// Store in cache
		xpCache.put(level, xpRequired);

		return xpRequired;
	}

	public double cumulativeXpForLevel(int level) {
		double totalXp = 0;
		for (int i = 1; i <= level; i++) {
			totalXp += xpRequiredForLevel(i);
		}
		return totalXp;
	}

	public double currentXpForLevel(int level) {
		if (level <= 1) {
			return 0; // Assume 0 XP for level 1
		}
		return cumulativeXpForLevel(level) - cumulativeXpForLevel(level - 1);
	}

	public double xpRequiredForNextLevel(int currentLevel) {
		return xpRequiredForLevel(currentLevel + 1);
	}

	public int getLevelFromXp(double currentXp) {
		int level = 1;
		while (level < 100 && currentXp >= cumulativeXpForLevel(level + 1)) {
			level++;
		}
		return level;
	}

	static {
		// Compute the CDF for each level
		double cumulativeProbability = 0.0;
		for (int level = 1; level <= MAX_LEVEL; level++) {
			cumulativeProbability += Math.exp(-LAMBDA * level);
			cdf.add(cumulativeProbability);
		}
		// Normalize the CDF
		double maxProbability = cdf.get(cdf.size() - 1);
		for (int i = 0; i < cdf.size(); i++) {
			cdf.set(i, cdf.get(i) / maxProbability);
		}
	}

	/**
	 * Get a random level for a newly caught champion based on a weighted
	 * distribution.
	 * 
	 * @return The random level.
	 */
	public int getRandomChampionLevel() {
		Random rand = new Random();
		double randValue = rand.nextDouble();
		for (int i = 0; i < cdf.size(); i++) {
			if (randValue <= cdf.get(i)) {
				return i + 1; // Levels start from 1
			}
		}
		return MAX_LEVEL; // Should not reach here, but just in case
	}

}
