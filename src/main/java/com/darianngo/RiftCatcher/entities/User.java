package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private Integer gold; // For shop

	@Column(name = "level")
	private Integer level = 1; // Default to level 1 for new users

	@Column(name = "xp")
	private Integer xp = 0; // Experience points for leveling up

	@OneToMany(mappedBy = "user")
	private List<CaughtChampion> caughtChampions;

	@Column(name = "first_interaction_time")
	private LocalDateTime firstInteractionTime; // Time when the user first interacted with the bot

	@Column(name = "has_signed_up")
	private Boolean hasSignedUp = false; // Flag to track if the user has completed the signup process

	private String starterChampion;

}
