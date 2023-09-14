package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionRarity;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Long> {
	@Query("SELECT c FROM Champion c WHERE LOWER(c.name) = LOWER(:name)")
	Champion findByNameIgnoreCase(String name);

	@Query("SELECT c FROM Champion c WHERE c.rarity = :rarity")
	List<Champion> findByRarity(@Param("rarity") ChampionRarity rarity);
}
