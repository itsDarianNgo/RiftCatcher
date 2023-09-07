package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.Skin;

@Repository
public interface SkinRepository extends JpaRepository<Skin, Long> {
	List<Skin> findByChampionAndRarity(Champion champion, String rarity);
}
