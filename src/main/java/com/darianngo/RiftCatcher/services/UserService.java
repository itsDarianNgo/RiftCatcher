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

	public void handleStartCommand(String userId, MessageReceivedEvent event) {
		if (userRepository.existsById(userId)) {
			// User already exists
			return;
		}

		User newUser = new User();
		newUser.setDiscordId(userId);
		newUser.setDiscordName(event.getAuthor().getName());
		newUser.setLastCatchTime(LocalDateTime.now());
		newUser.setChampionsCaught(0);
		newUser.setGold(0);

		userRepository.save(newUser);
	}
}
