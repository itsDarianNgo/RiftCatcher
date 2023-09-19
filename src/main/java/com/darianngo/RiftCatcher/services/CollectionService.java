package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CollectionService {

	private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private CaughtChampionRepository caughtChampionRepository;

	@Autowired
	private ChampionAndSkinRarityService championRarityService;

	@Transactional
	public void addStarterChampionToUser(String discordUserId, String championName) {
		User user = userRepository.findByDiscordId(discordUserId);
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		// Handle errors
		handlePotentialErrors(user, champion, discordUserId, championName);

		// Determine the starter champion's skin rarity
		ChampionSkinRarity skinRarity = championRarityService.determineSkinRarity(champion);
		// Fetch a random skin of the chosen rarity for the designated champion
		ChampionSkin starterChampionSkin = championRarityService.getRandomSkinByRarity(champion, skinRarity);

		// Add additional logic here to generate stats, etc. for the starter champion

		// Create a new CaughtChampion and save
		CaughtChampion newCaughtChampion = new CaughtChampion();
		newCaughtChampion.setUser(user);
		newCaughtChampion.setChampion(champion);
		newCaughtChampion.setCaughtAt(LocalDateTime.now());
		newCaughtChampion.setSkin(starterChampionSkin);
		newCaughtChampion.setStarter(true); // Mark this as a starter champion

		caughtChampionRepository.save(newCaughtChampion);
	}

	@Transactional
	public void addChampionToUser(String discordUserId, String championName) {
		User user = userRepository.findByDiscordId(discordUserId);

		// Check if the champion being added is the user's first, hence a starter
		boolean isStarterChampion = isFirstChampionForUser(user);

		if (isStarterChampion) {
			addStarterChampionToUser(discordUserId, championName);
			return;
		}

		// Fetch the champion from the database
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		// Handle errors
		handlePotentialErrors(user, champion, discordUserId, championName);

		// Create a new CaughtChampion and save
		CaughtChampion newCaughtChampion = new CaughtChampion();
		newCaughtChampion.setUser(user);
		newCaughtChampion.setChampion(champion);
		newCaughtChampion.setCaughtAt(LocalDateTime.now());

		caughtChampionRepository.save(newCaughtChampion);
	}

	private void handlePotentialErrors(User user, Champion champion, String discordUserId, String championName)
			throws EntityNotFoundException {
		if (user == null && champion == null) {
			logger.error("User with ID {} and Champion with name {} not found.", discordUserId, championName);
			throw new EntityNotFoundException("User and Champion not found.");
		} else if (user == null) {
			logger.error("User with ID {} not found.", discordUserId);
			throw new EntityNotFoundException("User not found.");
		} else if (champion == null) {
			logger.error("Champion with name {} not found.", championName);
			throw new EntityNotFoundException("Champion not found.");
		}
	}

	/**
	 * Determines if the given user is catching their first champion.
	 * 
	 * @param user The user in question.
	 * @return True if it's the user's first champion, false otherwise.
	 */
	private boolean isFirstChampionForUser(User user) {
		return caughtChampionRepository.findByUser(user).isEmpty();
	}

}