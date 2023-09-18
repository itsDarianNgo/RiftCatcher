package com.darianngo.RiftCatcher.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.services.ChampionCatchingService;
import com.darianngo.RiftCatcher.services.ChampionSelectService;
import com.darianngo.RiftCatcher.services.UserService;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class CommandListener extends ListenerAdapter {

	@Autowired
	private ChampionCatchingService championCatchingService;

	@Autowired
	private UserService userService;

	@Autowired
	private ChampionSelectService championSelectService;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getAuthor().isBot())
			return;

		String botMention = "<@" + event.getJDA().getSelfUser().getId() + ">";
		if (event.getMessage().getContentRaw().contains(botMention)) {
			String[] args = event.getMessage().getContentRaw().split("\\s+");

			// If no command is provided
			if (args.length <= 1) {
				userService.handleUserState(event);
				return;
			}

			String command = args[1].toLowerCase();
			switch (command) {
			case "start":
				userService.handleStartCommand(event);
				break;
			case "select":
				championSelectService.handleChampionSelect(event, args);
				break;
			case "catch":
			case "c":
				championCatchingService.handleCommand(event);
				break;
			default:
				event.getChannel().sendMessage("Unknown command!").queue();
				break;
			}
		}
	}
}
