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
	private final List<String> skinRarities = Arrays.asList("Common", "Rare", "Epic", "Legendary", "Mythic",
			"Ultimate");

	@Override
	public void run(String... args) {
		Random random = new Random();

		// Create 10 champions
		for (int i = 1; i <= 10; i++) {
			Champion champion = new Champion();
			champion.setName("Champion" + i);

			// Randomly assign a rarity to the champion
			champion.setRarity(championRarities.get(random.nextInt(championRarities.size())));
			Champion savedChampion = championRepository.save(champion);

			// Create 3 skins for each champion
			for (int j = 1; j <= 3; j++) {
				Skin skin = new Skin();
				skin.setChampion(savedChampion);
				skin.setName("Skin" + j + "For" + savedChampion.getName());

				// Randomly assign a rarity to the skin
				skin.setRarity(skinRarities.get(random.nextInt(skinRarities.size())));

				skinRepository.save(skin);
			}
		}
	}
}
