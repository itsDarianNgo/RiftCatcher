package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.dtos.ChampionDTO;
import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.entities.SpawnedChampion;
import com.darianngo.RiftCatcher.mappers.ChampionMapper;
import com.darianngo.RiftCatcher.mappers.SpawnEventMapper;
import com.darianngo.RiftCatcher.repositories.SpawnEventRepository;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
public class ChampionSpawningService {

	@Autowired
	private JDA jda;

	@Autowired
	private ChampionAndSkinRarityService championAndSkinRarityService;

	@Autowired
	private ServerConfigService serverConfigService;

	@Autowired
	private SpawnEventRepository spawnEventRepository;

	@Autowired
	private ChampionMapper championMapper;

	@Autowired
	private SpawnEventMapper spawnEventMapper;

	private static final Logger logger = LoggerFactory.getLogger(ChampionSpawningService.class);

	// Map to keep track of last spawn time for each channel
	private HashMap<String, Long> lastSpawnTimeMap = new HashMap<>();

	// Method to check if a channel is enabled for spawning
	private boolean isChannelEnabled(String serverId, String channelId) {
		List<String> enabledChannels = serverConfigService.getEnabledChannels(serverId);
		return enabledChannels.contains(channelId);
	}

	// Randomly spawn champions in enabled channels
	@Scheduled(fixedRate = 60000) // 1 minute = 60,000 milliseconds
	@Transactional // Ensuring atomicity
	public void spawnChampion() {
		System.out.println("Spawning champion...");
		for (TextChannel channel : jda.getTextChannels()) {
			String serverId = channel.getGuild().getId();
			if (!isChannelEnabled(serverId, channel.getId()))
				continue;

			long currentTime = System.currentTimeMillis();
			long lastSpawnTime = lastSpawnTimeMap.getOrDefault(channel.getId(), 0L);

			// Add randomness to spawn interval if a champion has been recently caught
			Random rand = new Random();
			long randomDelay = rand.nextInt(30000); // up to 30 seconds

			if (currentTime - lastSpawnTime + randomDelay >= 60000) {
				// It's time to spawn a new champion
				SpawnedChampion champion = championAndSkinRarityService.spawnChampion();

				// Create a new SpawnEvent entry
				SpawnEvent spawnEvent = new SpawnEvent();
				spawnEvent.setSpawnedChampion(champion);
				spawnEvent.setSpawnTime(LocalDateTime.now());
				spawnEvent.setDiscordChannelId(channel.getId());
				spawnEvent.setDiscordServerId(serverId);
				SpawnEvent savedSpawnEvent = spawnEventRepository.saveAndFlush(spawnEvent);

				// Convert saved entities to DTOs
				ChampionDTO championDTO = championMapper.spawnedChampionToChampionDTO(champion);
				System.out.println("DTO Skin Name: " + championDTO.getCurrentSkinName());
				System.out.println("DTO Skin Image URL: " + championDTO.getCurrentSkinImageUrl());
				;

				// Create and send the embed
				MessageEmbed messageEmbed = createChampionEmbed(championDTO);
				channel.sendMessageEmbeds(messageEmbed).queue();

				// Update last spawn time
				lastSpawnTimeMap.put(channel.getId(), currentTime);
			}
		}
	}

	// Create an embed for the spawned champion
	private MessageEmbed createChampionEmbed(ChampionDTO championDTO) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("A Wild Champion Has Appeared!");
		embedBuilder.setColor(Color.CYAN);
		embedBuilder.setDescription("Guess the champion and type `@RiftCatchers catch [Champion Name]` to catch it!");
		embedBuilder.addField("Champion", championDTO.getName(), true);
		embedBuilder.addField("Rarity", championDTO.getRarity(), true);
		embedBuilder.addField("Current Skin", championDTO.getCurrentSkinName(), true);
		embedBuilder.setImage(championDTO.getCurrentSkinImageUrl());

		return embedBuilder.build();
	}

}
