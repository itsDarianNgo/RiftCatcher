package com.darianngo.RiftCatcher.listeners;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.entities.SpawnEvent;
import com.darianngo.RiftCatcher.repositories.SpawnEventRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class CatchCommandListener extends ListenerAdapter {
	@Autowired
	private SpawnEventRepository spawnEventRepository;
	@Autowired
	private UserRepository userRepository;

	// Define a pattern to capture the bot mention
	private final Pattern mentionPattern = Pattern.compile("(?i)<@!?([0-9]+)> (catch|c) (.+)");

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String rawMessage = event.getMessage().getContentRaw().toLowerCase();

		// Convert the bot's ID to lower case
		String botId = event.getJDA().getSelfUser().getId().toLowerCase();

		// Use regex to match the command pattern
		Matcher matcher = mentionPattern.matcher(rawMessage);

		if (matcher.matches() && matcher.group(1).equals(botId)) {

			String userId = event.getAuthor().getId();

			String commandType = matcher.group(2); // Should be 'catch' or 'c'
			String championName = matcher.group(3).toLowerCase(); // The champion name

			String serverId = event.getGuild().getId();
			String channelId = event.getChannel().getId();

			// Retrieve the active SpawnEvent based on channelId and championName
			Optional<SpawnEvent> spawnEventOpt = spawnEventRepository
					.findActiveSpawnByDiscordChannelIdAndChampionName(championName, channelId);

			if (spawnEventOpt.isPresent()) {
				Long spawnEventId = spawnEventOpt.get().getId(); // Extract the spawnEventId
				System.out.println("Spawn event found: " + spawnEventOpt.get().getId());

				// Delegate the actual catching logic to CatchCommandService
//				catchCommandService.handleCatchCommand(userId, championName, spawnEventId, serverId, channelId);
			} else {
				// Handle case when there is no active spawn event
				System.out.println("No spawn event found for channelId: " + channelId + " and championName: "
						+ championName + " and serverID: " + serverId);
			}
		}
	}
}
