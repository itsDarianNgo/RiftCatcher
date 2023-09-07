package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.SpawnEvent;

@Repository
public interface SpawnEventRepository extends JpaRepository<SpawnEvent, Long> {
}
