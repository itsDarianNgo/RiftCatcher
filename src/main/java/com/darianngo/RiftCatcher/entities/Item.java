package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type; // Enum type for Skin, Chroma, etc.
	private Long referenceId; // Corresponding ID from the Skin or Chroma table or any other item type
	private String name;
	private Integer price;
	private LocalDateTime rotationEndTime; // Time until this item is available in the shop
}
