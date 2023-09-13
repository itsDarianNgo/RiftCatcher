package com.darianngo.RiftCatcher.services;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionRarity;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.repositories.ChampionRarityRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRarityRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRepository;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class ChampionAndSkinRarityService extends ListenerAdapter {

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private ChampionRarityRepository championRarityRepository;

	@Autowired
	private ChampionSkinRepository championSkinRepository;

	@Autowired
	private ChampionSkinRarityRepository championSkinRarityRepository;

	private Champion lastSpawnedChampion;

	private ChampionSkinRarity lastSpawnedSkinRarity;

	private LocalTime lastUltimateSkinSpawnTime;

	private ChampionSkin lastSpawnedSkin;

	private LocalTime lastUniqueSkinSpawnTime;

	private static final Logger logger = LoggerFactory.getLogger(ChampionAndSkinRarityService.class);

	public Champion spawnChampion() {

		// Step 1: Determine Champion Rarity
		ChampionRarity chosenRarity = determineChampionRarity();

		// Step 2: Fetch Random Champion of the Chosen Rarity
		Champion champion = getRandomChampionByRarity(chosenRarity);

		// Step 3: Determine Skin Rarity
		ChampionSkinRarity skinRarity = determineSkinRarity(champion);

		// Step 4: Fetch Random Skin of the Chosen Rarity for Champion
		ChampionSkin skin = getRandomSkinByRarity(champion, skinRarity);

		champion.setCurrentSkin(skin);
		skin.setChampion(champion);
		championSkinRepository.save(skin);
		championRepository.save(champion);

		return champion;
	}

	// Logic to determine the rarity of the champion to be spawned
	public ChampionRarity determineChampionRarity() {
		System.out.println("Determining champion rarity...");

		// Fetch all available rarities from the database
		List<ChampionRarity> allRarities = championRarityRepository.findAll();

		// Initialize probabilities
		Map<String, Double> rarityProbabilities = new HashMap<>();

		// Base probabilities (these could be adjusted)
		double commonProb = 0.80; // 80%
		double uncommonProb = 0.15; // 15%
		double rareProb = 0.03; // 3%
		double legendaryProb = 0.02; // 2%

		// 1. Time-based adjustments
		LocalTime now = LocalTime.now();
		if (now.isAfter(LocalTime.of(0, 0)) && now.isBefore(LocalTime.of(6, 0))) {
			legendaryProb *= 1.5;
		}

		// 2. Special Events
		if (isSpecialEventToday()) {
			legendaryProb *= 2.0;
		}

		// Set the probabilities (use string values or database IDs as the keys)
		rarityProbabilities.put("COMMON", commonProb);
		rarityProbabilities.put("UNCOMMON", uncommonProb);
		rarityProbabilities.put("RARE", rareProb);
		rarityProbabilities.put("LEGENDARY", legendaryProb);

		// Choose a rarity based on the probabilities
		String chosenRarityKey = chooseRarityBasedOnProbability(rarityProbabilities);

		// Fetch the corresponding ChampionRarity entity from the database
		ChampionRarity chosenRarity = championRarityRepository.findByRarity(chosenRarityKey);

		return chosenRarity;
	}

	private boolean isSpecialEventToday() {
		// Implement logic to check for special events
		return false;
	}

	private String chooseRarityBasedOnProbability(Map<String, Double> probabilities) {
		double total = 0;
		for (double prob : probabilities.values()) {
			total += prob;
		}

		double randomValue = Math.random() * total;
		double cumulativeProbability = 0.0;

		for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
			cumulativeProbability += entry.getValue();
			if (randomValue <= cumulativeProbability) {
				return entry.getKey();
			}
		}

		return "COMMON"; // Default, should not reach here
	}

	// Fetch a random champion of the chosen rarity from the database
	public Champion getRandomChampionByRarity(ChampionRarity rarity) {
		System.out.println("Determining random champion by rarity...");

		List<Champion> champions = championRepository.findByRarity(rarity);
		Map<Champion, Double> championProbabilities = new HashMap<>();

		// Base probability for each champion
		double baseProb = 1.0 / champions.size();

		for (Champion champion : champions) {
			// Set base probability
			championProbabilities.put(champion, baseProb);

			// 1. Recent Spawn Penalty
			if (champion.equals(lastSpawnedChampion)) {
				championProbabilities.put(champion, baseProb * 0.5);
			}

			// 2. Event-Based Boost (Assume a method to check this)
			if (isEventBoostedChampion(champion)) {
				championProbabilities.put(champion, baseProb * 1.5);
			}
		}

		// Choose a champion based on the adjusted probabilities
		Champion chosenChampion = chooseChampionBasedOnProbability(championProbabilities);

		// Update last spawned champion
		lastSpawnedChampion = chosenChampion;

		return chosenChampion;
	}

	private boolean isEventBoostedChampion(Champion champion) {
		// Implement logic to check if the champion is boosted during an event
		return false;
	}

	private <T> T chooseChampionBasedOnProbability(Map<T, Double> probabilities) {
		double total = 0;
		for (double prob : probabilities.values()) {
			total += prob;
		}

		double randomValue = Math.random() * total;
		double cumulativeProbability = 0.0;

		for (Map.Entry<T, Double> entry : probabilities.entrySet()) {
			cumulativeProbability += entry.getValue();
			if (randomValue <= cumulativeProbability) {
				return entry.getKey();
			}
		}

		return null; // Default, should not reach here
	}

	// Complex logic to determine the rarity of the skin to be spawned
	public ChampionSkinRarity determineSkinRarity(Champion champion) {
		System.out.println("Determining skin rarity...");

		// Fetch all available skin rarities from the database
		List<ChampionSkinRarity> allSkinRarities = championSkinRarityRepository.findAll();

		// Initialize probabilities
		Map<String, Double> skinRarityProbabilities = new HashMap<>();

		// Base probabilities (these could be adjusted)
		double commonProb = 0.92; // 92%
		double rareProb = 0.05; // 5%
		double epicProb = 0.02; // 2%
		double legendaryProb = 0.005; // 0.5%
		double mythicProb = 0.0025; // 0.25%
		double ultimateProb = 0.0005; // 0.05%

		// 1. Champion Rarity-based adjustments
		if ("LEGENDARY".equals(champion.getRarity().getRarity())) {
			// Legendary champions boost the chance of ultimate and mythic skins
			ultimateProb *= 1.0;
			mythicProb *= 1.0;
		}

		// 2. Time-based adjustments
		LocalTime now = LocalTime.now();
		if (now.isAfter(LocalTime.of(18, 0)) && now.isBefore(LocalTime.of(20, 0))) {
			// Evening time boosts the chance of premium and legendary skins
			epicProb *= 1.1;
			legendaryProb *= 1.2;
		}

		// 3. Recent Ultimate Skin Spawn Penalty
		if (lastUltimateSkinSpawnTime != null && now.isBefore(lastUltimateSkinSpawnTime.plusHours(1))) {
			// Reduce the chance if an ultimate skin was spawned within the last hour
			ultimateProb *= 0.5;
		}

		// Set the probabilities (use string values or database IDs as the keys)
		skinRarityProbabilities.put("COMMON", commonProb);
		skinRarityProbabilities.put("RARE", rareProb);
		skinRarityProbabilities.put("EPIC", epicProb);
		skinRarityProbabilities.put("LEGENDARY", legendaryProb);
		skinRarityProbabilities.put("MYTHIC", mythicProb);
		skinRarityProbabilities.put("ULTIMATE", ultimateProb);

		// Choose a skin rarity based on the probabilities
		String chosenSkinRarityKey = chooseRarityBasedOnProbability(skinRarityProbabilities);

		// Fetch the corresponding SkinRarity entity from the database
		ChampionSkinRarity chosenSkinRarity = championSkinRarityRepository.findByRarity(chosenSkinRarityKey);

		// Update last spawn time if ultimate
		if (chosenSkinRarity.getRarity().equals("ULTIMATE")) {
			lastUltimateSkinSpawnTime = now;
		}

		return chosenSkinRarity;
	}

	// Fetch a random skin of the chosen rarity for the designated champion
	public ChampionSkin getRandomSkinByRarity(Champion champion, ChampionSkinRarity rarity) {
		logger.info("Determining random skin by rarity for champion: {} and rarity: {}", champion.getName(),
				rarity.getRarity());

		List<ChampionSkin> skins = championSkinRepository.findByChampionAndSkinRarity(champion, rarity);
		logger.info("Fetched {} skins for champion: {} and rarity: {}", skins.size(), champion.getName(),
				rarity.getRarity());

		Map<ChampionSkin, Double> skinProbabilities = new HashMap<>();
		double baseProb = 1.0 / skins.size();

		for (ChampionSkin skin : skins) {
			logger.debug("Calculating probabilities for skin: {}", skin.getName());

			skinProbabilities.put(skin, baseProb);

			if (skin.equals(lastSpawnedSkin)) {
				skinProbabilities.put(skin, baseProb * 0.5);
				logger.debug("Applying recent skin penalty to skin: {}", skin.getName());
			}

			if (isEventBoostedSkin(skin)) {
				skinProbabilities.put(skin, baseProb * 1.5);
				logger.debug("Applying event-based boost to skin: {}", skin.getName());
			}

			LocalTime now = LocalTime.now();
			if (now.isAfter(LocalTime.of(18, 0)) && now.isBefore(LocalTime.of(20, 0))) {
				skinProbabilities.put(skin, baseProb * 1.1);
				logger.debug("Applying time-based boost to skin: {}", skin.getName());
			}
		}

		ChampionSkin chosenSkin = chooseSkinBasedOnProbability(skinProbabilities);
		logger.info("Chosen skin: {}", chosenSkin.getName());

		lastSpawnedSkin = chosenSkin;

		return chosenSkin;
	}

	private boolean isEventBoostedSkin(ChampionSkin skin) {
		// Implement logic to check if the skin is boosted during an event
		return false;
	}

	private ChampionSkin chooseSkinBasedOnProbability(Map<ChampionSkin, Double> probabilities) {
		double total = 0;
		for (double prob : probabilities.values()) {
			total += prob;
		}

		double randomValue = Math.random() * total;
		double cumulativeProbability = 0.0;

		for (Map.Entry<ChampionSkin, Double> entry : probabilities.entrySet()) {
			cumulativeProbability += entry.getValue();
			if (randomValue <= cumulativeProbability) {
				return entry.getKey();
			}
		}

		return null; // Default, should not reach here
	}

}
