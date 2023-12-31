package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.User;

@Repository
public interface CaughtChampionRepository extends JpaRepository<CaughtChampion, Long> {
	List<CaughtChampion> findByUser_Id(Long userId);

	String findByUser(User user);

	List<CaughtChampion> findByUser_Id(Long userId, Pageable pageable);

	long countByUser_Id(Long userId);
}
