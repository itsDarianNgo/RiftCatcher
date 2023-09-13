package com.darianngo.RiftCatcher.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionRarity;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Long> {
	Optional<Champion> findByName(String name);

	List<Champion> findByRarity(ChampionRarity rarity);
}
