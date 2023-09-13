package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;

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

	// Map to keep track of last spawn time for each channel
	private HashMap<String, Long> lastSpawnTimeMap = new HashMap<>();

	// Method to check if a channel is enabled for spawning
	private boolean isChannelEnabled(String serverId, String channelId) {
		List<String> enabledChannels = serverConfigService.getEnabledChannels(serverId);
		return enabledChannels.contains(channelId);
	}

	// Randomly spawn champions in enabled channels
	@Scheduled(fixedRate = 60000) // 1 minute = 60,000 milliseconds
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
				Champion champion = championAndSkinRarityService.spawnChampion();

				// Create and send the embed
				MessageEmbed messageEmbed = createChampionEmbed(champion);
				channel.sendMessageEmbeds(messageEmbed).queue();

				// Update last spawn time
				lastSpawnTimeMap.put(channel.getId(), currentTime);
			}
		}
	}

	// Create an embed for the spawned champion
	private MessageEmbed createChampionEmbed(Champion champion) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("A Wild Champion Has Appeared!");
		embedBuilder.setColor(Color.CYAN);
		embedBuilder.setDescription("Guess the champion and type `@RiftCatcher [Champion Name]` to catch it!");
		embedBuilder.addField("Champion", champion.getName(), true);
		embedBuilder.addField("Skin", champion.getCurrentSkin().getName(), true);
		embedBuilder.addField("Rarity", champion.getCurrentSkin().getSkinRarity().getRarity().toString(), true);

		return embedBuilder.build();
	}

}
