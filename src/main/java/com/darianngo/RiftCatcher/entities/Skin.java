package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "skins")
public class Skin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Champion champion;

	private String name;
	private String rarity; // Common, Rare, Epic, Legendary, Mythic, Ultimate
	private String description;
	private String imageUrl;
}
