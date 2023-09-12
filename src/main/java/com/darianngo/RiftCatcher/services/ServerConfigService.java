package com.darianngo.RiftCatcher.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darianngo.RiftCatcher.entities.ServerChannel;
import com.darianngo.RiftCatcher.repositories.ServerChannelRepository;

@Service
public class ServerConfigService {

	@Autowired
	private ServerChannelRepository serverChannelRepository;

	@Transactional
	public void enableSpawning(String serverId, String channelId) {
		ServerChannel serverChannel = serverChannelRepository.findByDiscordServerIdAndDiscordChannelId(serverId,
				channelId);
		if (serverChannel == null) {
			serverChannel = new ServerChannel();
			serverChannel.setDiscordServerId(serverId);
			serverChannel.setDiscordChannelId(channelId);
			serverChannel.setCreatedAt(LocalDateTime.now());
		}
		serverChannel.setEnabled(true);
		serverChannel.setUpdatedAt(LocalDateTime.now());
		serverChannelRepository.save(serverChannel);
	}

	@Transactional
	public void disableSpawning(String serverId, String channelId) {
		ServerChannel serverChannel = serverChannelRepository.findByDiscordServerIdAndDiscordChannelId(serverId,
				channelId);
		if (serverChannel != null) {
			serverChannel.setEnabled(false);
			serverChannel.setUpdatedAt(LocalDateTime.now());
			serverChannelRepository.save(serverChannel);
		}
	}

	public List<String> getEnabledChannels(String serverId) {
		List<ServerChannel> serverChannels = serverChannelRepository.findAllByDiscordServerIdAndIsEnabledTrue(serverId);
		List<String> enabledChannels = new ArrayList<>();
		for (ServerChannel sc : serverChannels) {
			enabledChannels.add(sc.getDiscordChannelId());
		}
		return enabledChannels;
	}

	public Set<String> getServerIds() {
		List<ServerChannel> allEnabledChannels = serverChannelRepository.findAllByIsEnabledTrue();
		Set<String> serverIds = new HashSet<>();
		for (ServerChannel sc : allEnabledChannels) {
			serverIds.add(sc.getDiscordServerId());
		}
		return serverIds;
	}
}
