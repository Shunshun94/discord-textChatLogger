package com.hiyoko.discord.bot.MessageLogger;

import java.io.IOException;

import com.hiyoko.discord.bot.DTO.Message;

public interface MessageLogger {
	long logging(Message message) throws IOException;
	String changeOutputPlace(String outputPlace) throws IOException;
	String getOutputPlace();
}
