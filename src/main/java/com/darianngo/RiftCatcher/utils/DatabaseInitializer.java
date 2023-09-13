package com.darianngo.RiftCatcher.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionRarity;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.repositories.ChampionRarityRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRarityRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRepository;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class DatabaseInitializer implements CommandLineRunner {

	@Autowired
	private ChampionRarityRepository championRarityRepository;

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private ChampionSkinRepository championSkinRepository;

	@Autowired
	private ChampionSkinRarityRepository championSkinRarityRepository;

	@Override
	public void run(String... args) throws Exception {
		// Check if the database is already initialized
		if (championRepository.count() > 0) {
			return; // Skip initialization if data already exists
		}

		// Initialize rarities FIRST
		initializeRarities();
		// Initialize Champions and Skins
		initializeChampionWithSkins("Garen", "COMMON");
		initializeChampionWithSkins("Ahri", "COMMON");
		initializeChampionWithSkins("Yasuo", "UNCOMMON");
		initializeChampionWithSkins("Lux", "UNCOMMON");
		initializeChampionWithSkins("Zed", "RARE");
		initializeChampionWithSkins("Thresh", "RARE");
		initializeChampionWithSkins("Teemo", "LEGENDARY");
		initializeChampionWithSkins("Annie", "LEGENDARY");
	}

	private void initializeRarities() {
		// Initialize Champion Rarities
		List<String> rarities = Arrays.asList("COMMON", "UNCOMMON", "RARE", "LEGENDARY", "ULTIMATE");
		for (String rarity : rarities) {
			ChampionRarity championRarity = new ChampionRarity();
			championRarity.setRarity(rarity);
			championRarityRepository.save(championRarity);
		}

		// Initialize Champion Skin Rarities
		List<String> skinRarities = Arrays.asList("COMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC", "ULTIMATE");
		for (String skinRarity : skinRarities) {
			ChampionSkinRarity entity = new ChampionSkinRarity();
			entity.setRarity(skinRarity);
			championSkinRarityRepository.save(entity);
		}
	}

	private void initializeChampionWithSkins(String championName, String championRarity) {
		ChampionRarity foundRarity = championRarityRepository.findByRarity(championRarity);
		Champion champion = new Champion();
		champion.setName(championName);
		champion.setRarity(foundRarity);
		champion = championRepository.saveAndFlush(champion); // Save and retrieve to get generated ID

		List<String> skinRarities = Arrays.asList("COMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC", "ULTIMATE");
		ChampionSkin firstSkin = null;
		for (String skinRarity : skinRarities) {
			ChampionSkinRarity foundSkinRarity = championSkinRarityRepository.findByRarity(skinRarity);

			ChampionSkin skin1 = new ChampionSkin();
			skin1.setName(championName + " Skin 1 " + skinRarity);
			skin1.setSkinRarity(foundSkinRarity);
			skin1.setChampion(champion);
			skin1 = championSkinRepository.saveAndFlush(skin1); // Save and retrieve to get generated ID

			if (firstSkin == null) {
				firstSkin = skin1;
			}

			ChampionSkin skin2 = new ChampionSkin();
			skin2.setName(championName + " Skin 2 " + skinRarity);
			skin2.setSkinRarity(foundSkinRarity);
			skin2.setChampion(champion);
			skin2 = championSkinRepository.saveAndFlush(skin2); // Save and retrieve to get generated ID
		}

		// Set the current skin for the champion to the first one created
		champion.setCurrentSkin(firstSkin);
		championRepository.saveAndFlush(champion); // Save again to update the currentSkin field
	}

}
