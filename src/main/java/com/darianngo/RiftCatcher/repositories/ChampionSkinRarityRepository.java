package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;

public interface ChampionSkinRarityRepository extends JpaRepository<ChampionSkinRarity, Long> {

	ChampionSkinRarity findByRarity(String chosenSkinRarityKey);
}
