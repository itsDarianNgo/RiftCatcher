package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@Table(name = "starter_champions")
//@AllArgsConstructor
public class StarterChampion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column(name = "region")
	private String region;

	@Column(name = "lore")
	private String lore;

	@Column(name = "bonus_name")
	private String bonusName;

	@Column(name = "bonus_description", length = 1000) // Assuming descriptions can be quite long
	private String bonusDescription;
}
