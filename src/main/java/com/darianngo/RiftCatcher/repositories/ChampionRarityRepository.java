package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darianngo.RiftCatcher.entities.ChampionRarity;

public interface ChampionRarityRepository extends JpaRepository<ChampionRarity, Long> {

	ChampionRarity findByRarity(String chosenRarityKey);
}
