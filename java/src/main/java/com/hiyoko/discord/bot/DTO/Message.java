package com.hiyoko.discord.bot.DTO;

import java.util.Calendar;
import java.util.List;

public class Message {
	public final Channel channel;
	public final String id;
	public final Calendar date;
	public final User user;
	public final List<AttachedFile> files;
	public final String message;
	public Message(String message, Channel channel, String messageId, User user, List<AttachedFile> files, Calendar date) {
		this.message = message;
		this.id = messageId;
		this.channel = channel;
		this.user = user;
		this.files = files;
		this.date = date;
	}
	public String toString() {
		return String.format("%s\t%s\t%s", user, channel, message);
	}
}
