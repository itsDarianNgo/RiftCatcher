package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.SpawnedChampion;

@Repository
public interface SpawnedChampionRepository extends JpaRepository<SpawnedChampion, Long> {

}
