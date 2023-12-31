package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.CaughtChampionRune;

@Repository
public interface CaughtChampionRuneRepository extends JpaRepository<CaughtChampionRune, Long> {
}
