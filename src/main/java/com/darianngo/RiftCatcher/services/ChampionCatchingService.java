package com.darianngo.RiftCatcher.services;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.SpawnEventRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
@RequiredArgsConstructor
public class ChampionCatchingService {

	private final Random random = new Random();

	@Autowired
	private final ChampionRepository championRepository;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final SpawnEventRepository spawnEventRepository;

	// Cooldown management
	private final Map<String, Long> lastCatchAttemptByUser = new ConcurrentHashMap<>();

	public void handleCommand(MessageReceivedEvent event) {
		String userId = event.getAuthor().getId();
		long currentTime = System.currentTimeMillis();

		// Check for cooldown
		if (lastCatchAttemptByUser.containsKey(userId) && currentTime - lastCatchAttemptByUser.get(userId) < 5000) {
			event.getChannel().sendMessage("Please wait a few seconds before trying to catch again!").queue();
			return;
		}

		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if (args.length < 3) {
			event.getChannel().sendMessage("Invalid command format! Use `@RiftCatcher catch <champion-name>`").queue();
			return;
		}

		String command = args[1];
		String championName = args[2];

		switch (command.toLowerCase()) {
		case "catch":
		case "c":
			catchChampion(championName, event);
			break;
		default:
			event.getChannel().sendMessage("Unknown command!").queue();
			break;
		}

		lastCatchAttemptByUser.put(userId, currentTime);
	}

	private synchronized void catchChampion(String championName, MessageReceivedEvent event) {
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		if (champion == null) {
			event.getChannel().sendMessage(championName + " does not exist!").queue();
			return;
		}

		// Fetch the latest spawn event for the champion
		SpawnEvent latestSpawn = spawnEventRepository.findLatestSpawnForChampion(champion.getId());
		System.out.println("Latest Spawn Details: " + latestSpawn);

		if (latestSpawn == null || latestSpawn.getCaughtByUserId() != null) {
			event.getChannel().sendMessage(championName + " is not available to catch!").queue();
			return;
		}

		boolean success = random.nextBoolean();

		if (success) {
			latestSpawn.setCaughtByUserId(event.getAuthor().getId());
			spawnEventRepository.save(latestSpawn);

			event.getChannel().sendMessage("Congratulations! You caught " + championName + "!").queue();
		} else {
			// The champion "flees" but is still available for others to attempt to catch
			event.getChannel().sendMessage(championName + " escaped! Better luck next time.").queue();
		}
	}

}