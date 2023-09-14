package com.darianngo.RiftCatcher.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.darianngo.RiftCatcher.dtos.ChampionDTO;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionRarity;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.Role;
import com.darianngo.RiftCatcher.entities.SpawnedChampion;

@Mapper(componentModel = "spring")
public interface ChampionMapper {

	ChampionDTO championToChampionDTO(Champion champion);

	@Mapping(source = "baseChampion.name", target = "name")
	@Mapping(source = "baseChampion.rarity.rarity", target = "rarity")
	@Mapping(source = "currentSkin", target = "currentSkinName", qualifiedByName = "mapCurrentSkinName")
	ChampionDTO spawnedChampionToChampionDTO(SpawnedChampion spawnedChampion);

	default String mapRarity(ChampionRarity rarity) {
		return rarity.getRarity();
	}

	default Set<String> mapRolesNames(Set<Role> roles) {
		return roles.stream().map(this::roleToRoleName).collect(Collectors.toSet());
	}

	// Reverse mapping methods
	default ChampionRarity mapRarity(String rarityName) {
		ChampionRarity championRarity = new ChampionRarity();
		championRarity.setRarity(rarityName);
		;
		return championRarity;
	}

	default Set<Role> mapToRoleSet(Set<String> roleNames) {
		return roleNames.stream().map(roleName -> {
			Role role = new Role();
			role.setRoleName(roleName);
			return role;
		}).collect(Collectors.toSet());
	}

	@Named("mapCurrentSkinName")
	default String mapCurrentSkinName(ChampionSkin currentSkin) {
		if (currentSkin != null) {
			return currentSkin.getName();
		}
		return "Default";
	}

	@Named("mapRolesNames")
	default String roleToRoleName(Role role) {
		return role.getRoleName();
	}
}
