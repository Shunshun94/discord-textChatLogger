package com.hiyoko.discord.bot;

import java.util.logging.Logger;

import org.javacord.api.DiscordApiBuilder;

import com.hiyoko.discord.bot.DTO.Channel;
import com.hiyoko.discord.bot.DTO.Message;
import com.hiyoko.discord.bot.DTO.User;
import com.hiyoko.discord.bot.MessageLogger.MessageLogger;
import com.hiyoko.discord.bot.MessageLogger.SimpleLogger;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		new DiscordApiBuilder().setToken(System.getenv("TEXT_CHAT_LOGGER_TOKEN")).login().thenAccept(api -> {
			Logger logger = Logger.getLogger(App.class.getName());
			MessageLogger textChatLogger = new SimpleLogger(); 
			api.addMessageCreateListener(event -> {
				Channel channel = new Channel(event.getChannel().getIdAsString(), event.getChannel().asServerTextChannel().get().getName());
				User user = new User(event.getMessageAuthor().getIdAsString(), event.getMessageAuthor().getName(), event.getMessageAuthor().getDisplayName());
				Message msg = new Message(
						event.getMessageContent(),
						channel,
						user);
				textChatLogger.logging(msg);
			});
		});
	}
}
