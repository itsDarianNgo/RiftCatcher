package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Champion champion;
	@ManyToOne
	private Skin skin;

	private LocalDateTime spawnTime;
	private LocalDateTime endTime;
	private Boolean isCaught;
	private String discordChannelId;
	private String catchingUserId;
}
