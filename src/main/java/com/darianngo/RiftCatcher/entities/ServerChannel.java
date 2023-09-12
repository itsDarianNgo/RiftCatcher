package com.darianngo.RiftCatcher.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "server_channels")
@Data
public class ServerChannel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String discordServerId;
	private String discordChannelId;

	private boolean isEnabled;
	private LocalDateTime createdAt; // When this entry was created
	private LocalDateTime updatedAt; // When this entry was last updated
}
