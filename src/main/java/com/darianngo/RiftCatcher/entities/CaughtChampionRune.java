package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "caught_champion_runes")
public class CaughtChampionRune {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "caught_champion_id")
	private CaughtChampion caughtChampion;

	@ManyToOne
	@JoinColumn(name = "rune_id")
	private Rune rune;

}
