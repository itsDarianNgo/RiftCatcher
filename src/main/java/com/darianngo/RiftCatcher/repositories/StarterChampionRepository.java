package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.StarterChampion;

@Repository
public interface StarterChampionRepository extends JpaRepository<StarterChampion, Long> {

	StarterChampion findByName(String chosenChampionName);

	StarterChampion findByNameIgnoreCase(String name);
}
