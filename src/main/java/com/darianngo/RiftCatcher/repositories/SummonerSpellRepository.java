package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.SummonerSpell;

@Repository
public interface SummonerSpellRepository extends JpaRepository<SummonerSpell, Long> {
}
