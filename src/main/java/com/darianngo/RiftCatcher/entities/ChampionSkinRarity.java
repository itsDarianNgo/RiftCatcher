package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ChampionSkinRarity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skin_rarity")
	private Long id;

	private String rarity; // Common, Rare, Epic, Legendary, Mythic, Ultimate
}
