package com.hiyoko.discord.bot.DTO;

public class Message {
	public final Channel channel;
	public final User user;
	public final String message;
	public Message(String message, Channel channel, User user) {
		this.message = message;
		this.channel = channel;
		this.user = user;
	}
	public String toString() {
		return String.format("%s\t%s\t%s", user, channel, message);
	}
}
