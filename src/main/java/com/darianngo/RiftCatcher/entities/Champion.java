package com.darianngo.RiftCatcher.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(of = { "id", "name", "rarity" })
@ToString(of = { "id", "name" })
public class Champion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	private ChampionRarity rarity;

	@ManyToOne
	private Stats baseStats;

	@ManyToMany
	private Set<Rune> runes;

	@ManyToMany
	private Set<SummonerSpell> summonerSpells;

	@OneToMany(mappedBy = "champion", cascade = CascadeType.ALL)
	private Set<ChampionSkin> skins;

	@ManyToMany
	private Set<Role> roles;

}
