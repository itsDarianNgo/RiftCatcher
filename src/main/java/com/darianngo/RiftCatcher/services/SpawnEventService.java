package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.Skin;
import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.SkinRepository;
import com.darianngo.RiftCatcher.repositories.SpawnEventRepository;
import com.darianngo.RiftCatcher.utils.RaritySelector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
public class SpawnEventService {
	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private SkinRepository skinRepository;

	@Autowired
	private SpawnEventRepository spawnEventRepository;

	@Autowired
	private JDA jda;

	@Autowired
	private RaritySelector raritySelector;

	@Autowired
	private ServerConfigService serverConfigService;

	private static final Logger logger = LoggerFactory.getLogger(SpawnEventService.class);

	@Scheduled(fixedRate = 60000) // Spawns every 1 minute
	public void spawnChampion() {
		logger.debug("Attempting to spawn a champion...");
		String championRarity = selectChampionRarity();
		Champion selectedChampion = selectChampionByRarity(championRarity);

		String skinRarity = selectSkinRarity();
		Skin selectedSkin = selectSkinByRarity(selectedChampion, skinRarity);

		announceAndSaveSpawn(selectedChampion, selectedSkin);
	}

	private String selectChampionRarity() {
		Map<String, Integer> rarityWeights = new HashMap<>();
		rarityWeights.put("Common", 50);
		rarityWeights.put("Rare", 30);
		rarityWeights.put("Epic", 15);
		rarityWeights.put("Legendary", 5);

		return raritySelector.weightedRandomSelection(rarityWeights);
	}

	private String selectSkinRarity() {
		Map<String, Integer> skinRarityWeights = new HashMap<>();
		skinRarityWeights.put("Common", 40);
		skinRarityWeights.put("Rare", 25);
		skinRarityWeights.put("Epic", 20);
		skinRarityWeights.put("Legendary", 10);
		skinRarityWeights.put("Mythic", 4);
		skinRarityWeights.put("Ultimate", 1);

		return raritySelector.weightedRandomSelection(skinRarityWeights);
	}

	private void announceAndSaveSpawn(Champion champion, Skin skin) {
		SpawnEvent spawnEvent = new SpawnEvent();
		spawnEvent.setChampion(champion);
		spawnEvent.setSkin(skin);
		spawnEvent.setSpawnTime(LocalDateTime.now());
		spawnEvent.setEndTime(LocalDateTime.now().plusMinutes(5));
		spawnEvent.setIsCaught(false);
		spawnEventRepository.save(spawnEvent);

		// Create an EmbedBuilder instance
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("A Wild Champion Has Appeared!"); // Set the title of the embed
		embedBuilder.setColor(Color.CYAN); // Set the color of the embed line

		// Add a description below the title
		embedBuilder.setDescription("Guess the champion and type `!catch [Champion Name]` to catch it!");

		// Add fields to the embed
		embedBuilder.addField("Champion", champion.getName(), true);
		embedBuilder.addField("Skin", skin.getName(), true);
		embedBuilder.addField("Rarity", skin.getRarity(), true);

		// Add an image (could be the champion's thumbnail)
		// embedBuilder.setThumbnail("URL_OF_THE_IMAGE");

		// Build the embed
		MessageEmbed messageEmbed = embedBuilder.build();

		// Announce in all enabled channels for all servers
		for (String serverId : serverConfigService.getServerIds()) {
			List<String> channels = serverConfigService.getEnabledChannels(serverId);
			for (String channelId : channels) {
				TextChannel channel = jda.getTextChannelById(channelId);
				// Send the embed message
				channel.sendMessageEmbeds(messageEmbed).queue();
			}
		}
	}

	private Champion selectChampionByRarity(String rarity) {
		List<Champion> champions = championRepository.findByRarity(rarity);
		if (champions.isEmpty()) {
			// Log the error for debugging and future reference
			logger.error("No champions found for the rarity: " + rarity);

			// Default to a common champion
			champions = championRepository.findByRarity("Common");
			if (champions.isEmpty()) {
				// Log this critical issue
				logger.error("No common champions found. This should never happen.");
				return null;
			}
		}
		return champions.get(new Random().nextInt(champions.size()));
	}

	private Skin selectSkinByRarity(Champion champion, String rarity) {
		List<Skin> skins = skinRepository.findByChampionAndRarity(champion, rarity);
		if (skins.isEmpty()) {
			// Log the error
			logger.error("No skins found for the champion: " + champion.getName() + " with rarity: " + rarity);

			// Default to a common skin
			skins = skinRepository.findByChampionAndRarity(champion, "Common");
			if (skins.isEmpty()) {
				// Log this critical issue
				logger.error(
						"No common skins found for champion: " + champion.getName() + ". This should never happen.");
				return null;
			}
		}
		return skins.get(new Random().nextInt(skins.size()));
	}

}
