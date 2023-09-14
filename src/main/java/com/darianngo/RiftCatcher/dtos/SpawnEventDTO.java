package com.darianngo.RiftCatcher.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpawnEventDTO {

	private Long id;
	private Long championId;
	private String championName;
	private String skinName;
	private String currentSkinName;
	private boolean available;
	private String caughtByUserId;
	private LocalDateTime spawnTime;
	private LocalDateTime endTime;
	private String discordChannelId;
	private String discordServerId;

}
