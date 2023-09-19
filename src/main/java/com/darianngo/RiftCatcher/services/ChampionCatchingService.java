package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.entities.SpawnedChampion;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
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

	@Autowired
	private final CaughtChampionRepository caughtChampionRepository;

	@Autowired
	UserService userService;

	// Cooldown management
	private final Map<String, Long> lastCatchAttemptByUser = new ConcurrentHashMap<>();

	public void handleCommand(MessageReceivedEvent event) {
		String userId = event.getAuthor().getId();
		long currentTime = System.currentTimeMillis();

		// Check if the user exists and has signed up
		if (!userService.isUserExistAndSignedUp(userId)) {
			userService.createUser(event.getAuthor());
			userService.promptUserToSignUp(event);
			return;
		}

		// Check for cooldown
		if (isUserOnCooldown(userId, currentTime)) {
			event.getChannel().sendMessage("Please wait a few seconds before trying to catch again!").queue();
			return;
		}

		String championName = extractChampionNameFromCommand(event.getMessage().getContentRaw());
		if (championName == null) {
			event.getChannel().sendMessage("Invalid command format! Use `@RiftCatcher catch <champion-name>`").queue();
			return;
		}

		catchChampion(championName, event);

		lastCatchAttemptByUser.put(userId, currentTime);
	}

	private String extractChampionNameFromCommand(String command) {
		String[] args = command.split("\\s+");
		return args.length >= 3 ? args[2] : null;
	}

	private boolean isUserOnCooldown(String userId, long currentTime) {
		return lastCatchAttemptByUser.containsKey(userId) && currentTime - lastCatchAttemptByUser.get(userId) < 5000;
	}

	@Transactional
	private synchronized void catchChampion(String championName, MessageReceivedEvent event) {
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		if (champion == null) {
			event.getChannel().sendMessage(championName + " does not exist!").queue();
			return;
		}

		// Fetch the latest spawn event for the champion
		SpawnEvent latestSpawn = spawnEventRepository.findLatestSpawnForChampion(champion.getId());

		if (isChampionNotAvailableToCatch(latestSpawn)) {
			event.getChannel().sendMessage(champion.getName() + " is not available to catch!").queue();
			return;
		}

		boolean success = random.nextBoolean();

		if (success) {
			markChampionAsCaught(latestSpawn, event.getAuthor().getId());
			incrementUserChampionCaughtCount(event.getAuthor().getId());

			// Fetch the current skin of the spawned champion
			SpawnedChampion spawnedChampion = latestSpawn.getSpawnedChampion();
			ChampionSkin skin = spawnedChampion.getCurrentSkin();

			createCaughtChampion(event.getAuthor().getId(), champion, skin); // Pass the skin as an argument

			String successMessage = String.format(
					"**Victory!** You've bound a level %d %s to your will. The stars of Targon shine in your favor!",
					champion.getLevel(), champion.getName());
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " " + successMessage).queue();
		} else {
			// Mentioning the user and sending the defeat message
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention()
							+ " **Defeat!** The ancient runes of Runeterra intervened, and " + champion.getName()
							+ " remains free.")
					.queue();
		}
	}

	private boolean isChampionNotAvailableToCatch(SpawnEvent latestSpawn) {
		return latestSpawn == null || latestSpawn.getCaughtByUserId() != null;
	}

	private void markChampionAsCaught(SpawnEvent latestSpawn, String userId) {
		latestSpawn.setCaughtByUserId(userId);
		spawnEventRepository.save(latestSpawn);
	}

	private void incrementUserChampionCaughtCount(String userId) {
		User user = userRepository.findByDiscordId(userId);
		// Assuming you have a championCaught field in the User entity
		user.setChampionsCaught(user.getChampionsCaught() + 1);
		userRepository.save(user);
	}

	private void createCaughtChampion(String userId, Champion champion, ChampionSkin skin) {
		CaughtChampion caughtChampion = new CaughtChampion();
		caughtChampion.setUser(userRepository.findByDiscordId(userId));
		caughtChampion.setChampion(champion);
		caughtChampion.setSkin(skin);
		caughtChampion.setCaughtAt(LocalDateTime.now());

		caughtChampionRepository.save(caughtChampion);
	}
}
