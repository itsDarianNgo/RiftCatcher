package com.darianngo.RiftCatcher.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darianngo.RiftCatcher.entities.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
	Optional<Shop> findTopByOrderByEventEndTimeDesc();
}
