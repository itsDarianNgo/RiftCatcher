package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public void handleStartCommand(MessageReceivedEvent event) {
		String userId = event.getAuthor().getId();
		User user = userRepository.findById(userId).orElse(null);

		if (user != null && user.getHasSignedUp()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You have already started your journey!")
					.queue();
			return;
		}

		// Logic to guide the user through the registration process, give initial
		// resources, etc.
		event.getChannel().sendMessage(
				event.getAuthor().getAsMention() + " Welcome to the world of Runeterra! Let's begin your journey...")
				.queue();

		// If the user is already created but hasn't signed up yet
		if (user == null) {
			user = createUser(event.getAuthor());
		}
		user.setHasSignedUp(true);
		userRepository.save(user);
	}

	public User createUser(net.dv8tion.jda.api.entities.User discordUser) {
		User newUser = new User();
		newUser.setDiscordId(discordUser.getId());
		newUser.setDiscordName(discordUser.getName());
		newUser.setFirstInteractionTime(LocalDateTime.now());
		return userRepository.save(newUser);
	}
}
