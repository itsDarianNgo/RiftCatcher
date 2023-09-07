package com.darianngo.RiftCatcher.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByType(String type);

	Optional<Item> findByIdAndType(Long id, String type);
}
