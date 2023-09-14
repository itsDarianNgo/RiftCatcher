package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class SpawnedChampion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Champion baseChampion; // The template of the champion that was spawned

	@ManyToOne
	private ChampionSkin currentSkin; // The skin the spawned champion is using

	private boolean available = true; // By default, a champion is available

}
