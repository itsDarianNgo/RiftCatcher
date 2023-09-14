package com.darianngo.RiftCatcher.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.darianngo.RiftCatcher.dtos.SpawnEventDTO;
import com.darianngo.RiftCatcher.entities.SpawnEvent;

@Mapper(componentModel = "spring", uses = { ChampionMapper.class })
public interface SpawnEventMapper {

	@Mapping(source = "spawnedChampion.id", target = "championId")
	@Mapping(source = "spawnedChampion.baseChampion.name", target = "championName")
	SpawnEventDTO spawnEventToSpawnEventDTO(SpawnEvent spawnEvent);

	// If needed, the reverse mapping:
	SpawnEvent spawnEventDTOToSpawnEvent(SpawnEventDTO spawnEventDTO);
}
