package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.config.RedisManager;
import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.IVs;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Service
public class ArcanumService {

	@Autowired
	private CaughtChampionRepository caughtChampionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RedisManager redisManager;

	private static final int PAGE_SIZE = 10; // Number of champions per page

	// Retrieve the list of champions for a user with pagination
	public List<CaughtChampion> getCaughtChampions(Long userId, int pageIndex) {
		return caughtChampionRepository.findByUser_Id(userId, PageRequest.of(pageIndex, PAGE_SIZE));
	}

	// Generate the arcanum embed with pagination
	public MessageEmbed generateArcanumEmbed(List<CaughtChampion> champions, int pageIndex, long totalChampions,
			boolean showSkins) {
		int totalPages = (int) Math.ceil((double) totalChampions / PAGE_SIZE);
		EmbedBuilder embed = new EmbedBuilder().setTitle("Your Arcanum (Champion Pool)").setColor(Color.MAGENTA)
				.setFooter("Page " + (pageIndex + 1) + " / " + totalPages);

		StringBuilder sb = new StringBuilder();
		int startIdx = pageIndex * PAGE_SIZE; // Calculate the starting index based on current page index

		for (int i = 0; i < champions.size(); i++) {
			CaughtChampion champ = champions.get(i);
			int number = startIdx + i + 1; // Start from the correct index, not always from 1
			String emoteId = champ.getChampion().getEmoteId();
			String emote = emoteId != null ? "<:__:" + emoteId + ">" : ":__:";
			String skin = champ.getSkin().getName();
			String skinRarityEmote = "";
			if (!"DEFAULT".equals(champ.getSkin().getSkinRarity().getRarity())) {
				String rarityEmoteId = champ.getSkin().getSkinRarity().getEmoteId();
				skinRarityEmote = rarityEmoteId != null ? "<:__:" + rarityEmoteId + ">" : ":__:";
			}

			double perfectIVPercentage = calculatePerfectIVPercentage(champ.getIvs());

			// Create a formatted string for the champion number with extra spacing
			String numPart = String.format("`%-3d`" + "　", number);

			// Create a formatted string for the champion name with extra spacing
			String namePart = String.format("`%-15s`" + "　" + "•" + "　", champ.getChampion().getName());

			// Create the rest of the string outside the inline code block
			String restOfRow = String.format("`Lvl. %-3d`" + "　" + "•" + "　" + "`%s%%`", champ.getLevel(),
					String.format("%05.2f", perfectIVPercentage));

			// Combine them together with the emote
			String row = numPart + emote + namePart + "    " + restOfRow;

			if (showSkins && !"DEFAULT".equals(champ.getSkin().getName().toUpperCase())) {
				// Add skins to your row if `showSkins` is true and the skin is not "Default"
				String skinPart = String.format("`%-15s`", champ.getSkin().getName());
				row += "　•　" + skinRarityEmote + skinPart;
			}

			sb.append(row).append("\n");
		}

		// Correctly showing entries
		int endIdx = startIdx + champions.size(); // Calculate the ending index based on current page index
		sb.append("Showing entries ").append(startIdx + 1).append("–").append(endIdx).append(" out of ")
				.append(totalChampions).append(".");

		embed.setDescription(sb.toString());
		return embed.build();
	}

	public void showArcanum(ButtonInteractionEvent event, int pageIndex, String originalMsgId, boolean showSkins) {
		event.deferEdit().queue();

		User user = userRepository.findByDiscordId(event.getUser().getId());
		if (user == null) {
			// Handle this scenario
			return;
		}

		List<CaughtChampion> champions = getCaughtChampions(user.getId(), pageIndex);
		long totalChampions = getTotalCaughtChampions(user.getId());
		boolean isLastPage = (pageIndex + 1) >= Math.ceil((double) totalChampions / PAGE_SIZE);
		MessageEmbed arcanumEmbed = generateArcanumEmbed(champions, pageIndex, totalChampions, showSkins);

		// Generate Arcanum pagination buttons
		Button prevButton = createArcanumButton("arcanum_page", pageIndex - 1, originalMsgId, "Previous",
				pageIndex == 0, showSkins);
		Button nextButton = createArcanumButton("arcanum_page", pageIndex + 1, originalMsgId, "Next", isLastPage,
				showSkins);
		Button toggleSkinsButton = createArcanumSecondaryButton("toggle_skins", pageIndex, originalMsgId,
				showSkins ? "Hide Skins" : "Show Skins", false, showSkins);

		// Edit the original message to show the new page
		event.getHook().editOriginalEmbeds(arcanumEmbed)
				.setComponents(ActionRow.of(prevButton, nextButton, toggleSkinsButton)).queue();
	}

	private Button createArcanumButton(String action, int pageIndex, String originalMsgId, String label,
			boolean isDisabled, boolean showSkins) {
		String customId = String.format("%s:%d:%s:%b", action, pageIndex, originalMsgId, showSkins);
		return isDisabled ? Button.primary(customId, label).asDisabled() : Button.primary(customId, label);
	}

	private Button createArcanumSecondaryButton(String action, int pageIndex, String originalMsgId, String label,
			boolean isDisabled, boolean showSkins) {
		String customId = String.format("%s:%d:%s:%b", action, pageIndex, originalMsgId, showSkins);
		return isDisabled ? Button.secondary(customId, label).asDisabled() : Button.secondary(customId, label);
	}

	// Get the total count of caught champions for pagination
	public long getTotalCaughtChampions(Long userId) {
		return caughtChampionRepository.countByUser_Id(userId);
	}

	public void handleArcanumCommand(MessageReceivedEvent event) {
		User user = userRepository.findByDiscordId(event.getAuthor().getId());
		if (user == null) {
			event.getChannel().sendMessage("User not found!").queue();
			return;
		}

		List<CaughtChampion> champions = getCaughtChampions(user.getId(), 0);
		long totalChampions = getTotalCaughtChampions(user.getId());
		boolean isLastPage = 1 >= Math.ceil((double) totalChampions / PAGE_SIZE);

		// Initialize the embed without showing skins (last parameter is `false`)
		MessageEmbed arcanumEmbed = generateArcanumEmbed(champions, 0, totalChampions, false);

		// Generate Arcanum pagination buttons
		// Include the showSkins state as `false` for these buttons
		Button prevButton = createArcanumButton("arcanum_page", 0, event.getMessageId(), "Previous", true, false);
		Button nextButton = createArcanumButton("arcanum_page", 1, event.getMessageId(), "Next", isLastPage, false);

		// New "Show Skins" button, initially set to `false`
		Button showSkinsButton = createArcanumSecondaryButton("toggle_skins", 0, event.getMessageId(), "Show Skins",
				false, false);

		// Send the initial arcanum embed
		event.getChannel().sendMessageEmbeds(arcanumEmbed)
				.setComponents(ActionRow.of(prevButton, nextButton, showSkinsButton)).queue();

		// Store user ID in Redis
		redisManager.setExpiringKey(event.getMessageId() + ":startCommandUser", event.getAuthor().getId(), 300);
	}

	public double calculatePerfectIVPercentage(IVs ivs) {
		// Define the maximum possible IVs for each stat
		final int maxHp = 31;
		final int maxAttack = 31;
		final int maxDefense = 31;
		final int maxSpAtk = 31;
		final int maxSpDef = 31;
		final int maxSpeed = 31;

		// Get the actual IVs from the caught champion
		int actualHp = ivs.getHpIV();
		int actualAttack = ivs.getAttackIV();
		int actualDefense = ivs.getDefenseIV();
		int actualSpAtk = ivs.getSpAtkIV();
		int actualSpDef = ivs.getSpDefIV();
		int actualSpeed = ivs.getSpeedIV();

		// Calculate the total maximum and actual IVs
		int totalMaxIv = maxHp + maxAttack + maxDefense + maxSpAtk + maxSpDef + maxSpeed;
		int totalActualIv = actualHp + actualAttack + actualDefense + actualSpAtk + actualSpDef + actualSpeed;

		// Calculate the percentage of perfection
		double perfectIVPercentage = ((double) totalActualIv / totalMaxIv) * 100;

		// Round to 2 decimal places
		perfectIVPercentage = Math.round(perfectIVPercentage * 100.0) / 100.0;

		return perfectIVPercentage;
	}

}
