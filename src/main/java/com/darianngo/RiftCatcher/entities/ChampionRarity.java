package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ChampionRarity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "champion_rarity")
	private Long id;

	private String rarity;
}
