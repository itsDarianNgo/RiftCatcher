package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CollectionService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private CaughtChampionRepository caughtChampionRepository;

	@Transactional
	public void addChampionToUser(String discordUserId, String championName) {
		// Fetch the user and champion from the database
		User user = userRepository.findByDiscordId(discordUserId);
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		if (user == null || champion == null) {
			// Handle error (user not found or champion not valid)
			return;
		}

		// Check if user already has this champion
		List<CaughtChampion> existingChampions = user.getCaughtChampions();
		if (existingChampions.stream()
				.anyMatch(champ -> champ.getChampion().getName().equalsIgnoreCase(championName))) {
			// User already has this champion. Logic can be adjusted based on game rules.
			return;
		}

		// Create a new CaughtChampion and save
		CaughtChampion newCaughtChampion = new CaughtChampion();
		newCaughtChampion.setUser(user);
		newCaughtChampion.setChampion(champion);
		newCaughtChampion.setCaughtAt(LocalDateTime.now());

		caughtChampionRepository.save(newCaughtChampion);
	}

}
