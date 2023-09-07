package com.darianngo.RiftCatcher.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;

@Service
public class ChampionSelector {
	@Autowired
	private ChampionRepository championRepository;

	public Champion selectChampion(String rarity) {
		List<Champion> champions = championRepository.findByRarity(rarity);

		int totalWeight = champions.stream().mapToInt(Champion::getSpawnWeight).sum();

		int randomValue = new Random().nextInt(totalWeight);

		int currentWeightSum = 0;
		for (Champion champion : champions) {
			currentWeightSum += champion.getSpawnWeight();
			if (randomValue < currentWeightSum) {
				return champion;
			}
		}
		return champions.get(0); // Default to the first champion if something goes wrong
	}
}
