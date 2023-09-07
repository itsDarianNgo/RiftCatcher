package com.darianngo.RiftCatcher.utils;

import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RaritySelector {
	public String weightedRandomSelection(Map<String, Integer> weights) {
		int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
		int randomIndex = new Random().nextInt(totalWeight);

		for (Map.Entry<String, Integer> entry : weights.entrySet()) {
			randomIndex -= entry.getValue();
			if (randomIndex < 0) {
				return entry.getKey();
			}
		}
		return null; // Should never reach here if weights are properly configured
	}
}
