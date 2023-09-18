package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.config.RedisManager;
import com.darianngo.RiftCatcher.entities.StarterChampion;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private StarterChampionRepository starterChampionRepository;

	@Autowired
	private ChampionSelectService championSelectService;

	@Autowired
	private RedisManager redisManager;

	public void handleStartCommand(MessageReceivedEvent event) {
		String userId = event.getAuthor().getId();
		User user = userRepository.findByDiscordId(userId);

		// If user doesn't exist, create them
		if (user == null) {
			user = createUser(event.getAuthor());
		}

		// Check if user has already signed up
		if (user.getHasSignedUp()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You have already started your journey!")
					.queue();
			return;
		}

		// Check if user has chosen a starter champion
		if (user.getChampionsCaught() != null && user.getChampionsCaught() > 0) {
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention() + " You have already selected your starter champion!")
					.queue();
			return;
		}

		// Save the user's ID in Redis using the message ID of the original command as a
		// key
		String key = event.getMessageId() + ":startCommandUser";
		redisManager.setExpiringKey(key, event.getAuthor().getId(), 300); // Expires in 5 minutes

		// Send paginated champion embeds to select from
		championSelectService.sendChampionEmbed(event, 0);
	}

	public void handleChampionSelect(MessageReceivedEvent event, String[] args) {
		String userId = event.getAuthor().getId();

		// Check if the user has already signed up
		if (isUserExistAndSignedUp(userId)) {
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention() + " You have already selected your starter champion!")
					.queue();
			return;
		}
		
		if (args.length < 3) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		String chosenChampionName = args[2];

		// Validate the champion choice by checking the database
		StarterChampion chosenChampion = starterChampionRepository.findByNameIgnoreCase(chosenChampionName);

		if (chosenChampion == null) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		// Use the exact name from the database for the response
		String actualChampionName = chosenChampion.getName();

		// Add the champion to the user's collection
		collectionService.addChampionToUser(event.getAuthor().getId(), actualChampionName);

		// Mark the user as signed up
		User user = userRepository.findByDiscordId(event.getAuthor().getId());
		if (user != null) {
			userRepository.save(user);
		}

		// Mark the user as signed up, update gold, last catch time, starter champion,
		// and increment champions caught
		user.setHasSignedUp(true);
		user.setGold(500);
		user.setLastCatchTime(LocalDateTime.now());
		user.setStarterChampion(actualChampionName);
		user.setChampionsCaught(1);

		userRepository.save(user);

		// Add the champion to the user's collection
		collectionService.addChampionToUser(event.getAuthor().getId(), actualChampionName);

		event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Congratulations! You have selected "
				+ actualChampionName + " as your starter champion!").queue();
	}

	private void sendInvalidChampionSelectionMessage(MessageReceivedEvent event) {
		event.getChannel().sendMessage(
				event.getAuthor().getAsMention() + " Invalid champion choice. Please select a valid starter champion.")
				.queue();
	}

	public User createUser(net.dv8tion.jda.api.entities.User discordUser) {
		User newUser = new User();
		newUser.setDiscordId(discordUser.getId());
		newUser.setDiscordName(discordUser.getName());
		newUser.setFirstInteractionTime(LocalDateTime.now());
		return userRepository.save(newUser);
	}

	public void handleUserState(MessageReceivedEvent event) {
		String userId = event.getAuthor().getId();
		User user = userRepository.findByDiscordId(userId);

		// If user doesn't exist
		if (user == null) {
			user = createUser(event.getAuthor());
		}

		// If user hasn't signed up yet
		if (!user.getHasSignedUp()) {
			promptUserToSignUp(event);
		}
	}

	public boolean isUserExistAndSignedUp(String userId) {
		User user = userRepository.findByDiscordId(userId);
		return user != null && user.getHasSignedUp();
	}

	public void promptUserToSignUp(MessageReceivedEvent event) {
		event.getChannel().sendMessage(event.getAuthor().getAsMention()
				+ " In the vast world of Runeterra, every summoner starts with a trusty champion by their side. Select yours with `@RiftCatcher Start` and begin your journey!")
				.queue();
	}
}
