package com.hiyoko.discord.bot.MessageLogger;

import java.io.IOException;

import com.hiyoko.discord.bot.DTO.Message;

public class SimpleLogger implements MessageLogger {
	private long count = 0;
	@Override
	public long logging(Message message) {
		System.out.println(message);
		count++;
		return count;
	}
	@Override
	public String changeOutputPlace(String outputPlace) throws IOException {
		return outputPlace; 
	}
	@Override
	public String getOutputPlace() {
		return "";
	}
}
