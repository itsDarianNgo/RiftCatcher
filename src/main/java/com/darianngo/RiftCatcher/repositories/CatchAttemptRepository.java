package com.darianngo.RiftCatcher.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darianngo.RiftCatcher.entities.CatchAttempt;
import com.darianngo.RiftCatcher.entities.SpawnEvent;

public interface CatchAttemptRepository extends JpaRepository<CatchAttempt, Long> {
	Optional<CatchAttempt> findByUserIdAndSpawnEvent(String userId, SpawnEvent spawnEvent);
}
