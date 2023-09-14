package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "spawn_events")
public class SpawnEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "spawned_champion_id")
	private SpawnedChampion spawnedChampion;


	@Column(name = "caught_by_user_id")
	private String caughtByUserId;

	private LocalDateTime spawnTime;
	private LocalDateTime endTime;
	private String discordChannelId;
	private String discordServerId;
}
