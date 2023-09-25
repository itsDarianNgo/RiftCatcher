package com.darianngo.RiftCatcher.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChampionDTO {

	private Long id;
	private String name;
	private String rarity;
	private Integer level;
	private Long statsId;
	private Long ivId;

	private Set<String> runeNames;
	private Set<String> summonerSpellNames;
	private Set<String> skinNames;
	private String currentSkinName;
	private String currentSkinImageUrl;
	private Set<String> roles;

	private String ownerId;

}
