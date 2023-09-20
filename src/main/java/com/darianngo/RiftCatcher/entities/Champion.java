package com.darianngo.RiftCatcher.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Champion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	private ChampionRarity rarity;

	@ManyToOne
	private Stats stats;

	@ManyToOne
	private IV iv;

	@ManyToMany
	private Set<Rune> runes;

	@ManyToMany
	private Set<SummonerSpell> summonerSpells;

	@OneToMany(mappedBy = "champion", cascade = CascadeType.ALL)
	private Set<ChampionSkin> skins;

	@ManyToMany
	private Set<Role> roles;

	@OneToOne
	private Nature nature;

	// Manually define hashCode and equals to ignore the runes, summonerSpells,
	// stats, iv, and roles fields
	@Override
	public int hashCode() {
		return Objects.hash(id, name, rarity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Champion champion = (Champion) o;
		return Objects.equals(id, champion.id) && Objects.equals(name, champion.name)
				&& Objects.equals(rarity, champion.rarity);
	}

	@Override
	public String toString() {
		return "Champion [id=" + id + ", name=" + name + "]";
	}
}
