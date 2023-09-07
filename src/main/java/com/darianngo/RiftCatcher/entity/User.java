package com.darianngo.RiftCatcher.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String discordId;

	@Column(name = "discord_name")
	private String discordName;
	private LocalDateTime lastCatchTime; // For cooldowns
	private Integer championsCaught;
	private Integer points; // For shop

	@OneToMany(mappedBy = "user")
	private List<CaughtChampion> caughtChampions;
}
