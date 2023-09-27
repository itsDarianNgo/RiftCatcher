package com.darianngo.RiftCatcher.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.darianngo.RiftCatcher.dtos.ArcanumDTO;
import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;

@Mapper(componentModel = "spring")
public interface ArcanumMapper {

	@Mapping(source = "skin", target = "skin")
	ArcanumDTO toDto(CaughtChampion caughtChampion);

	default String map(ChampionSkin skin) {
		return skin != null ? skin.getName() : null;
	}
}
