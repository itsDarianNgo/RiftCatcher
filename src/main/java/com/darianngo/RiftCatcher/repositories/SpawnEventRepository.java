package com.darianngo.RiftCatcher.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.SpawnEvent;

@Repository
public interface SpawnEventRepository extends JpaRepository<SpawnEvent, Long> {

	@Query("SELECT s FROM SpawnEvent s WHERE s.id = :spawnEventId AND s.champion.name = :championName AND s.discordChannelId = :channelId")
	List<SpawnEvent> findActiveSpawns(@Param("spawnEventId") Long spawnEventId,
			@Param("championName") String championName, @Param("channelId") String channelId);

	Optional<SpawnEvent> findActiveSpawnByDiscordChannelId(String discordChannelId);

	Optional<SpawnEvent> findActiveSpawnByDiscordChannelIdAndChampionName(String championName, String discordChannelId);

}
