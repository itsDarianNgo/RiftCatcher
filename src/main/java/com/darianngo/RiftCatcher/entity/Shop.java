package com.darianngo.RiftCatcher.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "shop_items")
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String itemType; // Enum type for Skin, Chroma, etc.
	private Long itemId; // Corresponding ID from the Skin or Chroma table
	private Integer price;
	private LocalDateTime rotationEndTime;
}
