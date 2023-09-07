package com.darianngo.RiftCatcher.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darianngo.RiftCatcher.services.ServerConfigService;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class AdminSlashCommandListener extends ListenerAdapter {
	@Autowired
	private ServerConfigService serverConfigService;

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Member member = event.getMember();

		// Check if the member is an admin
		if (!isAdmin(member)) {
			event.reply("You must be an admin to use this command.").setEphemeral(true).queue();
			return;
		}

		String commandName = event.getName();
		String serverId = event.getGuild().getId();
		String channelId = event.getChannel().getId();

		switch (commandName) {
		case "enablespawning":
			serverConfigService.enableSpawning(serverId, channelId);
			event.reply("Champion spawning enabled in this channel.").queue();
			break;
		case "disablespawning":
			serverConfigService.disableSpawning(serverId, channelId);
			event.reply("Champion spawning disabled in this channel.").queue();
			break;
		default:
			event.reply("Unknown command.").setEphemeral(true).queue();
			break;
		}
	}

	private boolean isAdmin(Member member) {
		// Check if the member has the ADMINISTRATOR permission
		return member != null && member.hasPermission(Permission.ADMINISTRATOR);
	}
}
