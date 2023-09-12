package com.darianngo.RiftCatcher.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.CatchAttempt;
import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.Rune;
import com.darianngo.RiftCatcher.entities.Skin;
import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.entities.SummonerSpell;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CatchAttemptRepository;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.RuneRepository;
import com.darianngo.RiftCatcher.repositories.SpawnEventRepository;
import com.darianngo.RiftCatcher.repositories.SummonerSpellRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
public class CatchCommandService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SpawnEventRepository spawnEventRepository;

	@Autowired
	private CaughtChampionRepository caughtChampionRepository;

	@Autowired
	private RuneRepository runeRepository;

	@Autowired
	private SummonerSpellRepository summonerSpellRepository;

	@Autowired
	private CatchAttemptRepository catchAttemptRepository;

	@Autowired
	private ServerConfigService serverConfigService;

	@Autowired
	@Lazy
	private JDA jda;

	@Transactional
	public void handleCatchCommand(String userId, String championName, Long spawnEventId, String serverId,
			String channelId) {
		// Validate the server and channel
		if (!isValidServerAndChannel(serverId, channelId)) {
			System.out.println("Invalid server or channel.");
			return;
		}

		// Check if the champion is currently spawned in the channel
		List<SpawnEvent> spawnEvents = spawnEventRepository.findActiveSpawns(spawnEventId, championName, channelId);
		if (spawnEvents.isEmpty()) {
			// Debug information
			System.out.println("No active spawn found.");
			System.out.println("Searching for champion: " + championName + " in channel: " + channelId);
			// Champion not found, or time expired
			sendMessageToChannel(channelId, "<@" + userId + ">" + ", the champion is not currently spawned.");
			return;
		}

		// Loop through all spawn events
		for (SpawnEvent spawnEvent : spawnEvents) {
			// Check if the user has already attempted to catch this spawn
			Optional<CatchAttempt> existingAttempt = catchAttemptRepository.findByUserIdAndSpawnEvent(userId,
					spawnEvent);
			if (existingAttempt.isPresent()) {
				sendMessageToChannel(channelId,
						"<@" + userId + ">" + ", you've already attempted to catch this champion.");
				continue; // Skip to the next iteration
			}

			// Attempt to catch
			CatchAttempt newAttempt = new CatchAttempt();
			newAttempt.setUserId(userId);
			newAttempt.setSpawnEvent(spawnEvent);

			if (attemptCatch(spawnEvent)) {
				// Success
				newAttempt.setSuccess(true);
				catchAttemptRepository.save(newAttempt);

				CaughtChampion caughtChampion = createCaughtChampion(userId, spawnEvent);
				caughtChampionRepository.save(caughtChampion);

				sendMessageToChannel(channelId, "<@" + userId + ">" + ", you have successfully caught a level "
						+ caughtChampion.getLevel() + " " + championName + "!");
			} else {
				// Failure
				newAttempt.setSuccess(false);
				catchAttemptRepository.save(newAttempt);

				sendMessageToChannel(channelId, "<@" + userId + ">" + ", the champion has escaped.");
			}
		}
	}

	private boolean isValidServerAndChannel(String serverId, String channelId) {
		// Check if the server and channel are valid for catching based on the server's
		// configuration
		return serverConfigService.getEnabledChannels(serverId).contains(channelId);
	}

	private boolean attemptCatch(SpawnEvent spawnEvent) {
		// Calculate catch rate based on various factors like champion's rarity, skin
		// rarity, etc.
		double catchRate = calculateCatchRate(spawnEvent);
		return new Random().nextDouble() < catchRate;
	}

	private double calculateCatchRate(SpawnEvent spawnEvent) {
		// Base catch rate
		double baseRate = 0.5;

		// Modify catch rate based on champion rarity
		switch (spawnEvent.getChampion().getRarity()) {
		case "Common":
			baseRate += 0.2;
			break;
		case "Rare":
			baseRate += 0.1;
			break;
		case "Epic":
			baseRate -= 0.1;
			break;
		case "Legendary":
			baseRate -= 0.2;
			break;
		// Add more cases if you have additional rarities
		}

		// Modify catch rate based on skin rarity
		switch (spawnEvent.getSkin().getRarity()) {
		case "Common":
			baseRate += 0.05;
			break;
		case "Rare":
			baseRate += 0.025;
			break;
		case "Epic":
			baseRate -= 0.025;
			break;
		case "Legendary":
			baseRate -= 0.05;
			break;
		case "Mythic":
			baseRate -= 0.075;
			break;
		case "Ultimate":
			baseRate -= 0.1;
			break;
		}

		// Ensure catch rate is within 0 and 1
		return Math.min(1.0, Math.max(0.0, baseRate));
	}

	private CaughtChampion createCaughtChampion(String userId, SpawnEvent spawnEvent) {
		// Debug: Log the userId
		System.out.println("Attempting to find user with ID: " + userId);
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		Champion champion = spawnEvent.getChampion();
		Skin skin = spawnEvent.getSkin();

		// Randomly select 2 runes and 2 summoner spells
		List<Rune> allRunes = runeRepository.findAll();
		Collections.shuffle(allRunes);
		List<Rune> selectedRunes = allRunes.subList(0, 2);

		List<SummonerSpell> allSpells = summonerSpellRepository.findAll();
		Collections.shuffle(allSpells);
		List<SummonerSpell> selectedSpells = allSpells.subList(0, 2);

		// Randomly generate a level between 1-100, higher levels are rarer
		int level = generateRandomLevel();

		// Create and populate the CaughtChampion entity
		CaughtChampion caughtChampion = new CaughtChampion();
		caughtChampion.setUser(user);
		caughtChampion.setChampion(champion);
		caughtChampion.setRunes(selectedRunes);
		caughtChampion.setSummonerSpells(selectedSpells);
		caughtChampion.setSkin(skin.getName());
		caughtChampion.setLevel(level);
		// ... populate other fields like health, mana based on level and runes/spells

		return caughtChampion;
	}

	private int generateRandomLevel() {
		// Use weighted random generation to make higher levels rarer
		int randomValue = new Random().nextInt(1000);

		if (randomValue < 600) {
			return new Random().nextInt(50) + 1; // Levels 1-50
		} else if (randomValue < 900) {
			return new Random().nextInt(30) + 51; // Levels 51-80
		} else {
			return new Random().nextInt(20) + 81; // Levels 81-100
		}
	}

	private void sendMessageToChannel(String channelId, String message) {
		// Send a message to the Discord channel using JDA
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel != null) {
			channel.sendMessage(message).queue();
		}
	}
}
