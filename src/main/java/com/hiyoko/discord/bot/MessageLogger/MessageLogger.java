package com.hiyoko.discord.bot.MessageLogger;

import com.hiyoko.discord.bot.DTO.Message;

public interface MessageLogger {
	long logging(Message message);
}
