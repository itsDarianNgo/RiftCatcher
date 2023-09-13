package com.darianngo.RiftCatcher.config;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.darianngo.RiftCatcher.listeners.AdminSlashCommandListener;
import com.darianngo.RiftCatcher.listeners.CatchCommandListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@PropertySource("classpath:application-secrets.properties")
public class JDAConfig {

	@Value("${DISCORD_BOT_TOKEN}")
	private String botToken;

	@Bean
	public JDA jda(AdminSlashCommandListener adminSlashCommandListener, CatchCommandListener catchCommandListener)
			throws LoginException {
		JDA jda = JDABuilder.createDefault(botToken)
				.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
						GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(adminSlashCommandListener, catchCommandListener).build();

		// Register slash commands after the JDA instance is built
		registerSlashCommands(jda);

		return jda;
	}

	private void registerSlashCommands(JDA jda) {
		jda.upsertCommand("enablespawning", "Enable champion spawning in this channel").queue();
		jda.upsertCommand("disablespawning", "Disable champion spawning in this channel").queue();
	}
}
