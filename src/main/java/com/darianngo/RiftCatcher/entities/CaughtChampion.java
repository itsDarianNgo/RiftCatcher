package com.darianngo.RiftCatcher.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "caught_champions")
public class CaughtChampion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;

	@ManyToOne
	private Champion champion;

	@ManyToMany
	@JoinTable(name = "caught_champion_runes", joinColumns = @JoinColumn(name = "caught_champion_id"), inverseJoinColumns = @JoinColumn(name = "rune_id"))
	private List<Rune> runes; // Should contain exactly 2 runes

	@ManyToMany
	@JoinTable(name = "caught_champion_spells", joinColumns = @JoinColumn(name = "caught_champion_id"), inverseJoinColumns = @JoinColumn(name = "spell_id"))
	private List<SummonerSpell> summonerSpells; // Should contain exactly 2 summoner spells

	private String skin;
	private Integer level;
	private Integer health;
	private Integer mana;
}
