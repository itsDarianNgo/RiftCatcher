package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.StarterChampion;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
	private RedisStateManagementService redisStateManagementService;

	private final int CHAMPIONS_PER_PAGE = 3; // or whatever number you want

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

		List<StarterChampion> champions = starterChampionRepository.findAll();
		MessageEmbed embed = generateStarterChampionEmbed(champions, 0);
		redisStateManagementService.setUserPage(userId, 0); // Initialize user's page to 0

		event.getChannel().sendMessageEmbeds(embed).setActionRows(getActionRow(userId)).queue();
	}

	}

	public MessageEmbed generateStarterChampionEmbed(List<StarterChampion> champions, int page) {
		EmbedBuilder embed = new EmbedBuilder().setTitle("Welcome to the world of Runeterra!").setColor(Color.CYAN)
				.setDescription(
						"To embark on your journey, select a starter champion using the `@RiftCatcher select <champion>` command.")
				.setFooter("Choose wisely, summoner!");

		int startIndex = page * 5; // 5 champions per page for example
		int endIndex = Math.min(startIndex + 5, champions.size());
		for (int i = startIndex; i < endIndex; i++) {
			StarterChampion champ = champions.get(i);
			String championContent = "**Lore:** *" + champ.getLore() + "*" + "\n\n**Bonus:** " + champ.getBonusName()
					+ " - " + champ.getBonusDescription();
			embed.addField(champ.getRegion() + " - **" + champ.getName() + "**", championContent, false);
		}

		return embed.build();
	}

	public void handleChampionSelect(MessageReceivedEvent event, String[] args) {
		if (args.length < 3) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		String chosenChampionName = args[2];

		// Validate the champion choice by checking the database
		StarterChampion chosenChampion = starterChampionRepository.findByName(chosenChampionName);

		if (chosenChampion == null) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		// Add the champion to the user's collection
		collectionService.addChampionToUser(event.getAuthor().getId(), chosenChampion.getName());

		// Mark the user as signed up
		User user = userRepository.findByDiscordId(event.getAuthor().getId());
		if (user != null) {
			user.setHasSignedUp(true);
			userRepository.save(user);
		}

		event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Congratulations! You have selected "
				+ chosenChampionName + " as your starter champion!").queue();
	}

	private void sendInvalidChampionSelectionMessage(MessageReceivedEvent event) {
		event.getChannel().sendMessage(
				event.getAuthor().getAsMention() + " Invalid champion choice. Please choose a valid starter champion.")
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
			event.getChannel().sendMessage(event.getAuthor().getAsMention()
					+ " In the vast world of Runeterra, every summoner starts with a trusty champion by their side. Select yours with `@RiftCatcher Start` and begin your journey!")
					.queue();
		}
	}

}
