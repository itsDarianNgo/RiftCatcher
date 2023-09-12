package com.darianngo.RiftCatcher.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.repositories.UserRepository;
import com.darianngo.RiftCatcher.services.CatchCommandService;
import com.darianngo.RiftCatcher.services.UserService;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class CommandListener extends ListenerAdapter {
	@Autowired
	private UserService userService;

	@Autowired
	private CatchCommandService catchCommandService;

	@Autowired
	private UserRepository userRepository;

	// Define a pattern to capture the bot mention
    private final Pattern mentionPattern = Pattern.compile("(?i)<@!?([0-9]+)>\\s*(start)\\s*(.*)?");

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String content = event.getMessage().getContentRaw();
		Matcher matcher = mentionPattern.matcher(content);

		if (matcher.matches()) {
			String botId = matcher.group(1);
			String command = matcher.group(2);
			String argument = matcher.group(3);
			String userId = event.getAuthor().getId();
			String username = event.getAuthor().getName();
			String serverId = event.getGuild().getId();
			String channelId = event.getChannel().getId();

			// Make sure the bot is the one being mentioned
			if (!botId.equals(event.getJDA().getSelfUser().getId())) {
				return;
			}

			if ("start".equalsIgnoreCase(command)) {
				userService.handleStartCommand(userId, event);
				event.getChannel()
						.sendMessage("Welcome to RiftCatcher, " + username + "! You can now start catching champions.")
						.queue();
			} else {
				event.getChannel()
						.sendMessage("Please use @RiftCatcher Start to create an account before catching champions.")
						.queue();
			}
		}
	}

}
