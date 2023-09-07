package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.CaughtChampion;

@Repository
public interface CaughtChampionRepository extends JpaRepository<CaughtChampion, Long> {
	List<CaughtChampion> findByUser_Id(Long userId);
}
