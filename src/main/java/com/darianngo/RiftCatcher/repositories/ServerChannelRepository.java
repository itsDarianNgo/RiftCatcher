package com.darianngo.RiftCatcher.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darianngo.RiftCatcher.entities.ServerChannel;

public interface ServerChannelRepository extends JpaRepository<ServerChannel, Long> {
	ServerChannel findByDiscordServerIdAndDiscordChannelId(String serverId, String channelId);

	List<ServerChannel> findAllByIsEnabledTrue();

	List<ServerChannel> findAllByDiscordServerIdAndIsEnabledTrue(String serverId);
}
