package com.darianngo.RiftCatcher.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class ServerConfigService {
	// Mapping between Discord server ID and a list of channel IDs
	private final Map<String, List<String>> serverChannelMapping = new HashMap<>();

	public void enableSpawning(String serverId, String channelId) {
		serverChannelMapping.computeIfAbsent(serverId, k -> new ArrayList<>()).add(channelId);
	}

	public void disableSpawning(String serverId, String channelId) {
		List<String> channels = serverChannelMapping.get(serverId);
		if (channels != null) {
			channels.remove(channelId);
		}
	}

	public List<String> getEnabledChannels(String serverId) {
		return serverChannelMapping.getOrDefault(serverId, Collections.emptyList());
	}

	public Set<String> getServerIds() {
		return serverChannelMapping.keySet();
	}

}
