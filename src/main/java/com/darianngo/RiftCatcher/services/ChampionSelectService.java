package com.darianngo.RiftCatcher.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.config.RedisManager;
import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.entities.IVs;
import com.darianngo.RiftCatcher.entities.Nature;
import com.darianngo.RiftCatcher.entities.Rune;
import com.darianngo.RiftCatcher.entities.StarterChampion;
import com.darianngo.RiftCatcher.entities.SummonerSpell;
import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.CaughtChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.NatureRepository;
import com.darianngo.RiftCatcher.repositories.RuneRepository;
import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;
import com.darianngo.RiftCatcher.repositories.SummonerSpellRepository;
import com.darianngo.RiftCatcher.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Service
@RequiredArgsConstructor
public class ChampionSelectService {

	private final Random random = new Random();

	private static final Logger logger = LoggerFactory.getLogger(ChampionSelectService.class);

	@Autowired
	private final StarterChampionRepository starterChampionRepository;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final RedisManager redisManager;

	@Autowired
	private final ChampionRepository championRepository;

	@Autowired
	private final ChampionAndSkinRarityService championAndSkinRarityService;

	@Autowired
	private final ChampionAttributesService championAttributesService;

	@Autowired
	private final CaughtChampionRepository caughtChampionRepository;

	@Autowired
	private final RuneRepository runeRepository;

	@Autowired
	private final NatureRepository natureRepository;

	@Autowired
	private final SummonerSpellRepository summonerSpellRepository;

	private static final int CHAMPIONS_PER_PAGE = 1;

	public void handleChampionSelect(MessageReceivedEvent event, String[] args) {
		String userId = event.getAuthor().getId();
		User user = userRepository.findByDiscordId(userId);

		// If user doesn't exist, create them
		if (user == null) {
			user = createUser(event.getAuthor());
		}
		// Check if the user has already signed up
		if (isUserExistAndSignedUp(userId)) {
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention() + " You have already selected your starter champion!")
					.queue();
			return;
		}

		if (args.length < 3) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		String chosenChampionName = args[2];

		// Validate the champion choice by checking the database
		StarterChampion chosenChampion = starterChampionRepository.findByNameIgnoreCase(chosenChampionName);

		if (chosenChampion == null) {
			sendInvalidChampionSelectionMessage(event);
			return;
		}

		// Use the exact name from the database for the response
		String actualChampionName = chosenChampion.getName();

		// Add the champion to the user's collection
		addStarterChampionToUser(event.getAuthor().getId(), actualChampionName);

		// Mark the user as signed up
		if (user != null) {
			userRepository.save(user);
		}

		// Mark the user as signed up, update gold, last catch time, starter champion,
		// and increment champions caught
		user.setHasSignedUp(true);
		user.setGold(500);
		user.setLastCatchTime(LocalDateTime.now());
		user.setStarterChampion(actualChampionName);
		user.setChampionsCaught(1);

		userRepository.save(user);

		event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Congratulations! You have selected "
				+ actualChampionName + " as your starter champion!").queue();
	}

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

	private void sendInvalidChampionSelectionMessage(MessageReceivedEvent event) {
		event.getChannel().sendMessage(
				event.getAuthor().getAsMention() + " Invalid champion choice. Please select a valid starter champion.")
				.queue();
	}

	private boolean isUserExistAndSignedUp(String userId) {
		User user = userRepository.findByDiscordId(userId);
		return user != null && user.getHasSignedUp();
	}

	public User createUser(net.dv8tion.jda.api.entities.User discordUser) {
		User newUser = new User();
		newUser.setDiscordId(discordUser.getId());
		newUser.setDiscordName(discordUser.getName());
		newUser.setFirstInteractionTime(LocalDateTime.now());
		return userRepository.save(newUser);
	}

	@Transactional
	public void addStarterChampionToUser(String discordUserId, String championName) {
		User user = userRepository.findByDiscordId(discordUserId);
		Champion champion = championRepository.findByNameIgnoreCase(championName);

		// Handle errors
		handlePotentialErrors(user, champion, discordUserId, championName);

		// Determine the starter champion's skin rarity
		ChampionSkinRarity skinRarity = championAndSkinRarityService.determineSkinRarity(champion);
		// Fetch a random skin of the chosen rarity for the designated champion
		ChampionSkin starterChampionSkin = championAndSkinRarityService.getRandomSkinByRarity(champion, skinRarity);

		Set<SummonerSpell> uniqueSummonerSpells = assignTwoUniqueSummonerSpells();
		Set<Rune> uniqueRunes = assignTwoUniqueRunes();
		IVs championIVs = championAttributesService.generateIVs();
		// Fetch a random nature when a champion is caught
		Nature randomNature = getRandomNature();

		// Create a new CaughtChampion and save
		CaughtChampion newCaughtChampion = new CaughtChampion();
		newCaughtChampion.setUser(user);
		newCaughtChampion.setChampion(champion);
		newCaughtChampion.setCaughtAt(LocalDateTime.now());
		newCaughtChampion.setSkin(starterChampionSkin);
		newCaughtChampion.setNature(randomNature);
		newCaughtChampion.setSummonerSpells(uniqueSummonerSpells);
		newCaughtChampion.setRunes(uniqueRunes);
		newCaughtChampion.setLevel(5);
		newCaughtChampion.setIvs(championIVs);

		newCaughtChampion.setStarter(true); // Mark this as a starter champion

		caughtChampionRepository.save(newCaughtChampion);
	}

	private void handlePotentialErrors(User user, Champion champion, String discordUserId, String championName)
			throws EntityNotFoundException {
		if (user == null && champion == null) {
			logger.error("User with ID {} and Champion with name {} not found.", discordUserId, championName);
			throw new EntityNotFoundException("User and Champion not found.");
		} else if (user == null) {
			logger.error("User with ID {} not found.", discordUserId);
			throw new EntityNotFoundException("User not found.");
		} else if (champion == null) {
			logger.error("Champion with name {} not found.", championName);
			throw new EntityNotFoundException("Champion not found.");
		}
	}

	private Set<Rune> assignTwoUniqueRunes() {
		List<Rune> allRunes = runeRepository.findAll();
		Collections.shuffle(allRunes);
		Set<Rune> uniqueRunes = new HashSet<>(allRunes.subList(0, 2)); // Assign the first two from the shuffled list
		return uniqueRunes;
	}

	private Set<SummonerSpell> assignTwoUniqueSummonerSpells() {
		List<SummonerSpell> allSpells = summonerSpellRepository.findAll();
		Collections.shuffle(allSpells);
		Set<SummonerSpell> uniqueSummonerSpells = new HashSet<>(allSpells.subList(0, 2));
		return uniqueSummonerSpells;
	}

	// Fetch a random nature for the champion
	private Nature getRandomNature() {
		List<Nature> allNatures = natureRepository.findAll();
		return allNatures.get(random.nextInt(allNatures.size()));
	}

}
