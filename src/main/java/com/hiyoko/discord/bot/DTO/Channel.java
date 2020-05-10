package com.hiyoko.discord.bot.DTO;

public class Channel {
	public final String id;
	public final String name;
	public Channel(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String toString() {
		return String.format("%s(%s)", name, id);
	}
}
