package com.darianngo.RiftCatcher.listeners;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.StarterChampion;
import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;
import com.darianngo.RiftCatcher.services.RedisStateManagementService;
import com.darianngo.RiftCatcher.services.UserService;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Service
public class ButtonInteractionListener {

	@Autowired
	private RedisStateManagementService redisStateManagementService;

	@Autowired
	private StarterChampionRepository starterChampionRepository;

	@Autowired
	private UserService userService;

	public void onButtonClick(ButtonInteractionEvent event) {
		if (!event.getUser().isBot()) {
			String userId = event.getUser().getId();
			int currentPage = redisStateManagementService.getUserPage(userId);

			switch (event.getComponentId()) {
			case "prev_page:":
				redisStateManagementService.setUserPage(userId, Math.max(0, currentPage - 1)); // decrement with lower
																								// limit of 0
				break;
			case "next_page:":
				redisStateManagementService.setUserPage(userId, currentPage + 1); // increment
				break;
			}

			List<StarterChampion> champions = starterChampionRepository.findAll();
			MessageEmbed updatedEmbed = userService.generateStarterChampionEmbed(champions,
					redisStateManagementService.getUserPage(userId));
			event.editMessageEmbeds(updatedEmbed).setActionRows(getActionRow(userId)).queue();
		}
	}

	public ActionRow getActionRow(String userId) {
		int currentPage = redisStateManagementService.getUserPage(userId);
		Button prevButton = currentPage == 0 ? Button.primary("prev_page:", "Previous").asDisabled()
				: Button.primary("prev_page:", "Previous");
		Button nextButton = Button.primary("next_page:", "Next"); // Add a condition to disable if on the last page

		return ActionRow.of(prevButton, nextButton);
	}

}
