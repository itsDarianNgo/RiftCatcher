package com.darianngo.RiftCatcher.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.Skin;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.SkinRepository;

@Component
public class DatabaseBootstrap implements CommandLineRunner {

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private SkinRepository skinRepository;

	private final List<String> championRarities = Arrays.asList("Common", "Rare", "Epic", "Legendary");

	@Override
	public void run(String... args) {
		Random random = new Random();

		// Create 10 champions
		for (int i = 1; i <= 10; i++) {
			Champion champion = new Champion();
			champion.setName("champion" + i);

			// Randomly assign a rarity to the champion
			champion.setRarity(championRarities.get(random.nextInt(championRarities.size())));
			Champion savedChampion = championRepository.save(champion);

			// Create one common skin for each champion
			Skin commonSkin = new Skin();
			commonSkin.setChampion(savedChampion);
			commonSkin.setName("CommonSkinFor" + savedChampion.getName());

			// Assign common rarity to the skin
			commonSkin.setRarity("Common");

			skinRepository.save(commonSkin);

			// Create additional skins if needed
			for (int j = 1; j <= 2; j++) {
				Skin skin = new Skin();
				skin.setChampion(savedChampion);
				skin.setName("Skin" + j + "For" + savedChampion.getName());

				// Randomly assign a rarity to the skin
				// Exclude 'Common' from the random selection
				List<String> otherRarities = Arrays.asList("Rare", "Epic", "Legendary", "Mythic", "Ultimate");
				skin.setRarity(otherRarities.get(random.nextInt(otherRarities.size())));

				skinRepository.save(skin);
			}
		}
	}
}
