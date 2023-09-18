package com.darianngo.RiftCatcher.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.config.RedisManager;
import com.darianngo.RiftCatcher.services.ChampionSelectService;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class ButtonListener extends ListenerAdapter {

	@Autowired
	private RedisManager redisManager;

	@Autowired
	private ChampionSelectService championSelectService;

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		try {
			String userId = event.getUser().getId();

			// Splitting the button ID to retrieve the action, page index, and original
			// message ID
			String[] parts = event.getComponentId().split(":");
			String action = parts[0];
			int pageIndex = Integer.parseInt(parts[1]);
			String originalMsgId = parts[2];

			String storedUserId = redisManager.retrieveValue(originalMsgId + ":startCommandUser");

			if (userId.equals(storedUserId)) {
				switch (action) {
				case "prev_page":
					championSelectService.sendChampionEmbed(event, pageIndex - 1, originalMsgId);
					break;
				case "next_page":
					championSelectService.sendChampionEmbed(event, pageIndex + 1, originalMsgId);
					break;
				}
			} else {
				event.reply("You're not authorized to interact with these buttons.").setEphemeral(true).queue();
			}
		} catch (Exception e) {
			event.reply("An error occurred while processing your request.").setEphemeral(true).queue();
			e.printStackTrace();
		}
	}
}