package com.hiyoko.discord.bot.MessageLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Append;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.BatchUpdate;

import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.hiyoko.discord.bot.DTO.AttachedFile;
import com.hiyoko.discord.bot.DTO.Message;
import com.hiyoko.discord.bot.MessageLogger.MessageLogger;

public class GoogleSpreadSheetLogger implements MessageLogger {
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	private final String CREDENTIALS_FILE_PATH;
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private String sheetId;
	private Sheets service;
	private final Logger logger = Logger.getLogger(GoogleSpreadSheetLogger.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final Pattern CURRENT_LINE_COUNT_PATTERN = Pattern.compile("(\\d+)$");

	public GoogleSpreadSheetLogger(String sheetId) throws GeneralSecurityException, IOException {
		this.sheetId = sheetId;
		logger.info(String.format("ログを保存する GSS の ID: %s", this.sheetId));
		String tmpPath = System.getenv("GSS_CREDENTIUALS_FILE_PATH");
		if(tmpPath == null) {
			CREDENTIALS_FILE_PATH = "credentials.json";
		} else {
			CREDENTIALS_FILE_PATH = tmpPath;
		}
		logger.info(String.format("%s を GSS への接続認証情報として利用します", CREDENTIALS_FILE_PATH));
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(HTTP_TRANSPORT))
				.setApplicationName("Discord Text Logger").build();
		initSheet();
	}

	private void initSheet() throws IOException {
		String range = "A:G";
		ValueRange getResult = service.spreadsheets().values().get(sheetId, range).execute();
		List<List<Object>> values = getResult.getValues();

		if (values != null && (! values.isEmpty())) {
			logger.info(String.format("GSS %s は初期化済みでした", sheetId));
			return;
		}

		List<Request> requests = new ArrayList<Request>();
		requests.add((new Request()).setDeleteDimension((new DeleteDimensionRequest()).setRange((new DimensionRange()).setStartIndex(1).setEndIndex(1000).setDimension("ROWS"))));
		requests.add((new Request()).setDeleteDimension((new DeleteDimensionRequest()).setRange((new DimensionRange()).setStartIndex(7).setEndIndex(26).setDimension("COLUMNS"))));
		BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
		service.spreadsheets().batchUpdate(sheetId, body).execute();

		List<ValueRange> data = new ArrayList<>();
		data.add(new ValueRange().setRange("A1").setValues(Arrays.asList(Arrays.asList(
				"発言ID",
				"チャンネルID",
				"ユーザID",
				"チャネル名",
				"ユーザ名",
				"発言日時",
				"内容",
				"メモ"))));
		BatchUpdateValuesRequest requestBody = new BatchUpdateValuesRequest();
	    requestBody.setValueInputOption("RAW");
	    requestBody.setData(data);
	    BatchUpdate request = service.spreadsheets().values().batchUpdate(sheetId, requestBody);
	    request.execute();
	}

	private long addRow(List<Object> contents) throws IOException {
		try {
			Append request = service.spreadsheets().values().append(
					sheetId,
					"A1", 
					new ValueRange().setValues(Arrays.asList(contents)));
			request.setValueInputOption("RAW");
			request.setInsertDataOption("INSERT_ROWS");
			AppendValuesResponse result = request.execute();
			Matcher matchResult = CURRENT_LINE_COUNT_PATTERN.matcher(result.getTableRange());
			matchResult.find();
			return Long.parseLong(matchResult.group(1));
		} catch (IOException e) {
			throw new IOException(String.format("Failed to write [%s/%s by %s aka %s] to https://docs.google.com/spreadsheets/d/%s/ ",
					contents.get(1), contents.get(0), contents.get(2), contents.get(3), sheetId), e);
		}
	}

	@Override
	public long logging(Message message) throws IOException {
		List<Object> inputs = new ArrayList<Object>();
		inputs.add((Object) message.id);
		inputs.add((Object) message.channel.id);
		inputs.add((Object) message.user.id);
		inputs.add((Object) message.channel.name);
		inputs.add((Object) message.user.nickname);
		inputs.add((Object) sdf.format(message.date.getTime())); // 時間
		inputs.add((Object) message.message);
		
		long lines = 0; 
		try {
			lines = addRow(inputs);
			for( AttachedFile file : message.files ) {
				inputs.set(6, file.toString());
				lines = addRow(inputs);
			}
		} catch (IOException e) {
			throw e;
		}
		return lines;
	}

	private Credential getCredential(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		File credentialFile = new File(CREDENTIALS_FILE_PATH);
		if(! credentialFile.exists()) {
			logger.info(String.format("Google Spread Sheet に接続するための認証情報ファイル %s が見つかりませんでした", CREDENTIALS_FILE_PATH));
			throw new IOException(String.format("%s is not found", CREDENTIALS_FILE_PATH));
		}
		try(InputStream in = new FileInputStream(credentialFile);) {
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
					clientSecrets, SCOPES)
							.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
							.setAccessType("offline").build();
			LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}
	}

	private void runAsStandAlone() throws IOException {
		String range = "A:G";
		ValueRange getResult = service.spreadsheets().values().get(sheetId, range).execute();
		List<List<Object>> values = getResult.getValues();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			System.out.println("Name, Major");
			for (List<Object> row : values) {
				// Print columns A and E, which correspond to indices 0 and 4.
				System.out.println(String.format("%s @ %s\n"
						+ "------------------------------------\n%s\n"
						+ "====================================",
						row.get(4), row.get(3), row.get(6)));
			}
		}
	}

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		String sheetId = null;
		if(args.length > 0) {
			sheetId = args[0];
		} else {
			sheetId = System.getenv("SHEET_ID");
		}
		if(sheetId == null) {
			throw new IOException("Sheet ID is not found");
		} else {
			(new GoogleSpreadSheetLogger(sheetId)).runAsStandAlone();
		}
	}

	@Override
	public String changeOutputPlace(String outputPlace) throws IOException {
		sheetId = outputPlace;
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(HTTP_TRANSPORT))
				.setApplicationName("Google Sheets API Java Quickstart").build();
		initSheet();
		return sheetId;
	}

	@Override
	public String getOutputPlace() {
		return sheetId;
	}
}
