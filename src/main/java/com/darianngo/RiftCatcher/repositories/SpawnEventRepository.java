package com.darianngo.RiftCatcher.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.SpawnEvent;

@Repository
public interface SpawnEventRepository extends JpaRepository<SpawnEvent, Long> {

	@Query("SELECT s FROM SpawnEvent s WHERE s.id = :spawnEventId AND s.spawnedChampion.baseChampion.name = :championName AND s.discordChannelId = :channelId")
	List<SpawnEvent> findActiveSpawns(@Param("spawnEventId") Long spawnEventId,
			@Param("championName") String championName, @Param("channelId") String channelId);

	Optional<SpawnEvent> findActiveSpawnByDiscordChannelId(String discordChannelId);

	@Query("select se from SpawnEvent se where se.discordChannelId = ?1 and se.spawnedChampion.baseChampion.name = ?2")
	Optional<SpawnEvent> findActiveSpawnByDiscordChannelIdAndChampionName(String discordChannelId, String championName);

	@Query("SELECT se FROM SpawnEvent se WHERE se.spawnedChampion.baseChampion.id = :championId ORDER BY se.spawnTime DESC")
	List<SpawnEvent> findSpawnsForChampion(@Param("championId") Long championId, Pageable pageable);

	default SpawnEvent findLatestSpawnForChampion(Long championId) {
		return findSpawnsForChampion(championId, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
	}

}
