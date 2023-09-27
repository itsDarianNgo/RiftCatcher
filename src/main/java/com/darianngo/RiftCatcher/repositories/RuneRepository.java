package com.darianngo.RiftCatcher.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.darianngo.RiftCatcher.entities.Rune;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

public interface RuneRepository extends JpaRepository<Rune, Long>, CustomRuneRepository {
	// Existing methods
}

interface CustomRuneRepository {
	Rune merge(Rune detachedRune);
}

class CustomRuneRepositoryImpl implements CustomRuneRepository {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	@Override
	public Rune merge(Rune detachedRune) {
		return entityManager.merge(detachedRune);
	}
}
