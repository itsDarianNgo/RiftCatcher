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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(exclude = { "caughtChampionRunes" })
@Table(name = "caught_champions")
public class CaughtChampion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;

	@ManyToOne
	private Champion champion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nature_id")
	private Nature nature;

	@OneToMany(mappedBy = "caughtChampion", cascade = CascadeType.ALL)
	private Set<CaughtChampionRune> caughtChampionRunes;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	@JoinTable(name = "caught_champion_spells", joinColumns = @JoinColumn(name = "caught_champion_id"), inverseJoinColumns = @JoinColumn(name = "summoner_spell_id"))
	private Set<SummonerSpell> summonerSpells;

	private LocalDateTime caughtAt;

	private boolean isStarter = false;

	@ManyToOne
	@JoinColumn(name = "skin_id")
	private ChampionSkin skin;
	private Integer level = 1;
	private Integer currentExperience = 0;
	private Integer experienceToNextLevel;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ivs_id")
	private IVs ivs;

	private Integer currentHp;
	private Integer currentAttack;
	private Integer currentDefense;
	private Integer currentSpAtk;
	private Integer currentSpDef;
	private Integer currentSpeed;

}
