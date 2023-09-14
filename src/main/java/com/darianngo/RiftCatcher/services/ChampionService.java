package com.darianngo.RiftCatcher.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.dtos.ChampionDTO;
import com.darianngo.RiftCatcher.mappers.ChampionMapper;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;

@Service
public class ChampionService {
	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private ChampionMapper championMapper;

	public ChampionDTO getChampion(Long id) {
		return championRepository.findById(id).map(championMapper::championToChampionDTO).orElse(null);
	}
}
