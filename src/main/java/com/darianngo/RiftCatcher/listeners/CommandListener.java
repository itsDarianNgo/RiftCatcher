package com.darianngo.RiftCatcher.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.services.ChampionCatchingService;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class CommandListener extends ListenerAdapter {

	private final ChampionCatchingService championCatchingService;

	@Autowired
	public CommandListener(ChampionCatchingService catchingService) {
		this.championCatchingService = catchingService;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;

		String botMention = "<@" + event.getJDA().getSelfUser().getId() + ">";
		if (event.getMessage().getContentRaw().contains(botMention)) {
			championCatchingService.handleCommand(event);
		}

	}
}
