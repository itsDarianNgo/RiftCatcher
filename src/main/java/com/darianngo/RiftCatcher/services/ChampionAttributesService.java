package com.darianngo.RiftCatcher.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.IVs;

@Service
public class ChampionAttributesService {

	public IVs generateIVs() {
		Random rand = new Random();
		IVs ivs = new IVs();

		ivs.setHpIV(rand.nextInt(32));
		ivs.setAttackIV(rand.nextInt(32));
		ivs.setDefenseIV(rand.nextInt(32));
		ivs.setSpAtkIV(rand.nextInt(32));
		ivs.setSpDefIV(rand.nextInt(32));
		ivs.setSpeedIV(rand.nextInt(32));

		return ivs;
	}

	public Map<String, Double> adjustStatsWithNature(Map<String, Double> baseStats, String boostedStat,
			String reducedStat) {
		Map<String, Double> adjustedStats = new HashMap<>();

		for (Map.Entry<String, Double> entry : baseStats.entrySet()) {
			String stat = entry.getKey();
			double value = entry.getValue();

			if (stat.equals(boostedStat)) {
				adjustedStats.put(stat, value * 1.1);
			} else if (stat.equals(reducedStat)) {
				adjustedStats.put(stat, value * 0.9);
			} else {
				adjustedStats.put(stat, value);
			}
		}

		return adjustedStats;
	}

}
