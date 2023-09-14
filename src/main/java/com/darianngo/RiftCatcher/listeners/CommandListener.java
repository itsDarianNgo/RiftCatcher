package com.darianngo.RiftCatcher.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.entities.User;
import com.darianngo.RiftCatcher.repositories.UserRepository;
import com.darianngo.RiftCatcher.services.ChampionCatchingService;
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
	private UserRepository userRepository;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getAuthor().isBot())
			return;

		String botMention = "<@" + event.getJDA().getSelfUser().getId() + ">";
		if (event.getMessage().getContentRaw().contains(botMention)) {
			String userId = event.getAuthor().getId();
			User user = userRepository.findByDiscordId(userId);

			if (user == null) {
				// Create a new user with default values
				user = userService.createUser(event.getAuthor());
			}

			if (!user.getHasSignedUp()) {
				event.getChannel().sendMessage(event.getAuthor().getAsMention()
						+ " In the vast world of Runeterra, every summoner starts with a trusty champion by their side. Select yours with `@RiftCatcher Start` and begin your journey!")
						.queue();
				return;
			}

			if (event.getMessage().getContentRaw().contains(botMention)) {
				String[] args = event.getMessage().getContentRaw().split("\\s+");
				if (args.length > 1) {
					String command = args[1].toLowerCase();
					switch (command) {
					case "start":
						userService.handleStartCommand(event);
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
	}
}
