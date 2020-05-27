package com.hiyoko.discord.bot.DTO;

public class AttachedFile {
	private final String url;
	private final String title;
	public AttachedFile(String title, String url) {
		this.url = url;
		this.title = title;
	}
	public String toString() {
		return String.format("%s (%s)", title, url);
	}
}
