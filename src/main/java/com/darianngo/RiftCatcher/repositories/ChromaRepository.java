package com.darianngo.RiftCatcher.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Chroma;

@Repository
public interface ChromaRepository extends JpaRepository<Chroma, Long> {
}
