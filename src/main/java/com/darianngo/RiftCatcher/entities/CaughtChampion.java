package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@OneToOne
	private Nature nature;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "caught_champion_runes", joinColumns = @JoinColumn(name = "caught_champion_id"), inverseJoinColumns = @JoinColumn(name = "rune_id"))
	private Set<Rune> runes; // Should contain exactly 2 runes

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "caught_champion_spells", joinColumns = @JoinColumn(name = "caught_champion_id"), inverseJoinColumns = @JoinColumn(name = "summoner_spell_id"))
	private Set<SummonerSpell> summonerSpells;

	private LocalDateTime caughtAt;

	private boolean isStarter = false;

	@ManyToOne
	@JoinColumn(name = "skin_id")
	private ChampionSkin skin;
	private Integer level;
	private Integer health;
	private Integer mana;

}
