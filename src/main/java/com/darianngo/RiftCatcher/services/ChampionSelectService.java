package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.config.RedisManager;
import com.darianngo.RiftCatcher.entities.StarterChampion;
import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Service
public class ChampionSelectService {

	@Autowired
	private StarterChampionRepository starterChampionRepository;

	@Autowired
	private RedisManager redisManager;

	private static final int CHAMPIONS_PER_PAGE = 1;

	public void sendChampionEmbed(MessageReceivedEvent event, int pageIndex) {
		EmbedBuilder embed = generateChampionEmbed(pageIndex);
		Button prevButton = createPrevButton(pageIndex, event.getMessageId());
		Button nextButton = createNextButton(pageIndex, starterChampionRepository.count(), event.getMessageId());

		event.getChannel().sendMessageEmbeds(embed.build()).setComponents(ActionRow.of(prevButton, nextButton)).queue();
	}

	public void sendChampionEmbed(ButtonInteractionEvent event, int pageIndex, String originalMsgId) {
		event.deferEdit().queue();
		EmbedBuilder embed = generateChampionEmbed(pageIndex);
		Button prevButton = createPrevButton(pageIndex, originalMsgId);
		Button nextButton = createNextButton(pageIndex, starterChampionRepository.count(), originalMsgId);

		// Edit the message that received the button click
		event.getHook().editOriginalEmbeds(embed.build()).setComponents(ActionRow.of(prevButton, nextButton)).queue();
	}

	private EmbedBuilder generateChampionEmbed(int pageIndex) {
		List<StarterChampion> champions = starterChampionRepository.findAll();

		EmbedBuilder embed = new EmbedBuilder().setTitle("Welcome to the world of Runeterra!").setColor(Color.CYAN)
				.setDescription(
						"To embark on your journey, select a starter champion using the `@RiftCatcher select <champion>` command.")
				.setFooter("Page " + (pageIndex + 1) + " / " + ((champions.size() - 1) / CHAMPIONS_PER_PAGE + 1));

		int startIndex = pageIndex * CHAMPIONS_PER_PAGE;
		int endIndex = Math.min(startIndex + CHAMPIONS_PER_PAGE, champions.size());

		for (int i = startIndex; i < endIndex; i++) {
			StarterChampion champ = champions.get(i);
			String championContent = "**Lore:** " + champ.getLore() + "\n\n**Bonus:** " + champ.getBonusName() + " - "
					+ champ.getBonusDescription();
			embed.addField(champ.getRegion() + " - " + champ.getName(), championContent, false);
		}

		return embed;
	}

	private Button createPrevButton(int pageIndex, String originalMsgId) {
		return (pageIndex == 0)
				? Button.primary("prev_page:" + pageIndex + ":" + originalMsgId, "Previous").asDisabled()
				: Button.primary("prev_page:" + pageIndex + ":" + originalMsgId, "Previous");
	}

	private Button createNextButton(int pageIndex, long totalChampions, String originalMsgId) {
		int totalPages = (int) Math.ceil((double) totalChampions / CHAMPIONS_PER_PAGE);
		return (pageIndex + 1 == totalPages)
				? Button.primary("next_page:" + pageIndex + ":" + originalMsgId, "Next").asDisabled()
				: Button.primary("next_page:" + pageIndex + ":" + originalMsgId, "Next");
	}

}
