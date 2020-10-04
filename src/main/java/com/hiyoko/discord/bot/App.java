package com.hiyoko.discord.bot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.event.message.MessageEditEvent;

import com.hiyoko.discord.bot.DTO.AttachedFile;
import com.hiyoko.discord.bot.DTO.Channel;
import com.hiyoko.discord.bot.DTO.Message;
import com.hiyoko.discord.bot.DTO.User;
import com.hiyoko.discord.bot.MessageLogger.GoogleSpreadSheetLogger;
import com.hiyoko.discord.bot.MessageLogger.MessageLogger;

/**
 * Hello world!
 *
 */
public class App {
	private Pattern GSS_ID_PATTERN = Pattern.compile("^([a-zA-Z\\d_-]+)$");
	private String BOT_CHANNEL = "Bot への DM";
	private Logger logger = Logger.getLogger(App.class.getName());
	private String sheetId;
	private String discordToken;
	private MessageLogger textChatLogger;
	private long botOwnerId;
	private DiscordApi botApi;
	
	
	private void post(Message msg) {
		try {
			long currentLines = textChatLogger.logging(msg);
			if(currentLines > 400000 && (currentLines % 1000 < 3)) {
				botApi.getUserById(botOwnerId).get().sendMessage(String.format("GSS [%s]　使用済み行 %s行 / 625000行\nそろそろ別の GSS を用意してください\nhttps://docs.google.com/spreadsheets/d/%s/", sheetId, currentLines, sheetId));
				botApi.getUserById(botOwnerId).get().sendMessage(String.format("Sheet の ID (`%s` 等) を DM で送信すれば Sheet を切り替えられます", sheetId));
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to logging", e);
		} catch ( ExecutionException e) {
			logger.log(Level.WARNING, String.format("Failed to send DM to %s", botOwnerId), e);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, String.format("Failed to send DM to %s", botOwnerId), e);
		}
	}

	private Message generateDeleteMessage(MessageDeleteEvent event) {
		User user;
		try {
			MessageAuthor author = event.getMessageAuthor().get();
			user = new User(author.getIdAsString(), author.getName(), author.getDisplayName());
		} catch (NoSuchElementException e) {
			user = new User("不明な ID", "不明な名前", "不明な名前");
		}
		List<AttachedFile> files = new ArrayList<AttachedFile>();
		Calendar timeStump = Calendar.getInstance();
		try {
			timeStump.setTimeInMillis(event.getMessage().get().getLastEditTimestamp().get().getEpochSecond() * 1000);
		} catch (NoSuchElementException e) {
			// 何もせず、現在時刻を使う
		}
		Channel channel;
		try {
			channel = new Channel(event.getChannel().getIdAsString(), event.getChannel().asServerTextChannel().get().getName());

		} catch (NoSuchElementException e) {
			channel = new Channel(BOT_CHANNEL, BOT_CHANNEL);
		}
		String messageContent;
		try {
			messageContent = String.format("[削除]%s", event.getMessage().get().getContent());
		} catch (NoSuchElementException e) {
			messageContent = "削除されたメッセージを取得できませんでした";
		}
		String messageId;
		try {
			messageId = event.getMessage().get().getIdAsString();
		} catch (NoSuchElementException e) {
			messageId = "削除されたメッセージの ID を取得できませんでした";
		}
		Message msg = new Message(
				messageContent,
				channel,
				messageId,
				user,
				files,
				timeStump);
		return msg;
	}

	private Message generateEditMessage(MessageEditEvent event) {
		User user;
		try {
			MessageAuthor author = event.getMessageAuthor().get();
			user = new User(author.getIdAsString(), author.getName(), author.getDisplayName());
		} catch (NoSuchElementException e) {
			user = new User("不明な ID", "不明な名前", "不明な名前");
		}
		List<AttachedFile> files = new ArrayList<AttachedFile>();
		Calendar timeStump = Calendar.getInstance();
		try {
			timeStump.setTimeInMillis(event.getMessage().get().getLastEditTimestamp().get().getEpochSecond() * 1000);
		} catch (NoSuchElementException e) {
			// 何もせず、現在時刻を使う
		}
		Channel channel;
		try {
			channel = new Channel(event.getChannel().getIdAsString(), event.getChannel().asServerTextChannel().get().getName());

		} catch (NoSuchElementException e) {
			channel = new Channel(BOT_CHANNEL, BOT_CHANNEL);
		}
		String messageContent;
		try {
			messageContent = String.format("[編集]%s", event.getMessage().get().getContent());
		} catch (NoSuchElementException e) {
			messageContent = "編集後のメッセージを取得できませんでした";
		}
		String messageId;
		try {
			messageId = event.getMessage().get().getIdAsString();
		} catch (NoSuchElementException e) {
			messageId = "編集されたメッセージの ID を取得できませんでした";
		}
		Message msg = new Message(
				messageContent,
				channel,
				messageId,
				user,
				files,
				timeStump);
		return msg;
	}
	
	private Message generatePostMessage(MessageCreateEvent event) {
		Channel channel = new Channel(event.getChannel().getIdAsString(), event.getChannel().asServerTextChannel().get().getName());
		User user = new User(event.getMessageAuthor().getIdAsString(), event.getMessageAuthor().getName(), event.getMessageAuthor().getDisplayName());
		List<AttachedFile> files = new ArrayList<AttachedFile>();
		List<MessageAttachment> attachements = event.getMessage().getAttachments();
		for( MessageAttachment attachedFile : attachements ) {
			files.add(new AttachedFile(attachedFile.getFileName(), attachedFile.getUrl().toString()));
		}
		Calendar timeStump = Calendar.getInstance();
		timeStump.setTimeInMillis(event.getMessage().getCreationTimestamp().getEpochSecond() * 1000);
		Message msg = new Message(
				event.getMessageContent(),
				channel,
				event.getMessage().getIdAsString(),
				user,
				files,
				timeStump);
		return msg;
	}

	private void prepare() throws IOException {
		sheetId = System.getenv("SHEET_ID");
		if(sheetId == null) {
			logger.warning("環境変数 SHEET_ID にログを蓄積するための Google Spread Sheet の ID を入力してください");
			logger.warning("例えば GSS の URL が https://docs.google.com/spreadsheets/d/5PI-EnSY7YtLjm7PlvU5egk_5Ec85K1fagaR-Re7HsAx/ なら\nID は 5PI-EnSY7YtLjm7PlvU5egk_5Ec85K1fagaR-Re7HsAx です");
			throw new IOException("Google Spread Sheet の ID 取得に失敗しました");
		}

		discordToken = System.getenv("TEXT_CHAT_LOGGER_TOKEN");
		if(discordToken == null) {
			logger.warning("環境変数 TEXT_CHAT_LOGGER_TOKEN に Discord Bot の Token を入力してください");
			logger.warning("Discord Bot の Token 取得手順は https://shunshun94.github.io/shared/sample/discordBot_101.html に解説を掲載しています");
			throw new IOException("Discord Bot の情報取得に失敗しました");
		}

		try {
			textChatLogger = new GoogleSpreadSheetLogger(sheetId);
		} catch (GeneralSecurityException e) {
			logger.log(Level.WARNING, String.format("Failed to create sheet to %s", sheetId), e);
			throw new IOException("Google Spread Sheet へのアクセスに失敗しました", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, String.format("Failed to create sheet to %s", sheetId), e);
			throw new IOException("Google Spread Sheet へのアクセスに失敗しました", e);
		}
	}

	public void run(String[] args) throws GeneralSecurityException, IOException {
		prepare();
		new DiscordApiBuilder().setToken(discordToken).login().thenAccept(api -> {
			botApi = api;
			botOwnerId = botApi.getOwnerId();
			botApi.addMessageDeleteListener(event -> {
				Message msg = generateDeleteMessage(event);
				if(! msg.channel.id.equals(BOT_CHANNEL)) { // Bot への DM の削除はスルーする
					post(msg);
				}
			});
			botApi.addMessageEditListener(event -> {
				Message msg = generateEditMessage(event);
				if(! msg.channel.id.equals(BOT_CHANNEL)) { // Bot への DM の編集はスルーする
					post(msg);
				}
			});
			botApi.addMessageCreateListener(event -> {
				if(event.isServerMessage()) {
					Message msg = generatePostMessage(event);
					post(msg);
				} else {
					if(event.getMessageAuthor().getId() == botOwnerId) {
						Matcher matchResult = GSS_ID_PATTERN.matcher(event.getMessageContent());
						if( matchResult.find() ) {
							String oldSheetId = textChatLogger.getOutputPlace();
							String newSheetId = matchResult.group(1);
							try {
								textChatLogger.changeOutputPlace(newSheetId);
								api.getUserById(botOwnerId).get().sendMessage(String.format("出力先を更新しました\n"
										+ "`Before`: https://docs.google.com/spreadsheets/d/%s/\n"
										+ "` After`: https://docs.google.com/spreadsheets/d/%s/", oldSheetId, newSheetId));
							} catch (IOException e) {
								logger.log(Level.WARNING, String.format("Failed to change sheet to %s", newSheetId), e);
							} catch ( ExecutionException e) {
								logger.log(Level.WARNING, String.format("Failed to send DM to %s but sheet output place is updated", botOwnerId), e);
							} catch (InterruptedException e) {
								logger.log(Level.WARNING, String.format("Failed to send DM to %s but sheet output place is updated", botOwnerId), e);
							}
						}
					}
				}
			});
		});
	}

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		(new App()).run(args);
	}
}
