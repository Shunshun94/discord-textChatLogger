package com.hiyoko.discord.bot.DTO;

public class User {
	public final String id;
	public final String name;
	public final String nickname;
	public User(String id, String name, String nickname) {
		this.id = id;
		this.name = name;
		this.nickname = nickname;
	}
	public User(String id, String name) {
		this.id = id;
		this.name = name;
		this.nickname = name;
	}
	public String toString() {
		return String.format("%s(%s)", nickname, id);
	}
}
