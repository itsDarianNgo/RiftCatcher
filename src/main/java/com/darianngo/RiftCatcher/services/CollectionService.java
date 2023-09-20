package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.entities.SummonerSpell;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.SummonerSpellRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CollectionService {

	private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private CaughtChampionRepository caughtChampionRepository;

	@Autowired
	private ChampionAndSkinRarityService championRarityService;

	@Autowired
	private SummonerSpellRepository summonerSpellRepository;




}