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

//		// Check if user has already signed up
//		if (user.getHasSignedUp()) {
//			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You have already started your journey!")
//					.queue();
//			return;
//		}
//
//		// Check if user has chosen a starter champion
//		if (user.getChampionsCaught() != null && user.getChampionsCaught() > 0) {
//			event.getChannel()
//					.sendMessage(event.getAuthor().getAsMention() + " You have already selected your starter champion!")
//					.queue();
//			return;
//		}

		// Save the user's ID in Redis using the message ID of the original command as a
		// key
		String key = event.getMessageId() + ":startCommandUser";
		redisManager.setExpiringKey(key, event.getAuthor().getId(), 3000); // Expires in 5 minutes

		// Send paginated champion embeds to select from
		championSelectService.sendChampionEmbed(event, 0);
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
