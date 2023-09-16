package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByDiscordId(String discordId);

	@Transactional
	default void addChampionToUser(String discordId, String championName, ChampionRepository championRepository,
			CaughtChampionRepository caughtChampionRepository) {
		User user = findByDiscordId(discordId);
		if (user == null) {
			// Handle error: User not found
			return;
		}
	}
}
