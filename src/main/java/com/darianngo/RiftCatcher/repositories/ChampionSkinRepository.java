package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;

@Repository
public interface ChampionSkinRepository extends JpaRepository<ChampionSkin, Long> {
	List<ChampionSkin> findByChampionAndSkinRarity(Champion champion, ChampionSkinRarity skinRarity);

	ChampionSkin findFirstByChampion(Champion champion);

	ChampionSkin findByChampion_NameAndName(String championName, String skinName);

}
