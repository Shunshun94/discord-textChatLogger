# Discord Text Logger

## どうやって使うの

1. [DiscordTextLogger.jar](https://drive.google.com/open?id=13yeQ_CW6vtuuyOY1DaxgY9k5Vifbt1fR) をダウンロードする
1. Discord Bot を作り Token を取得する ([Discord の Bot を作ろう！](https://shunshun94.github.io/shared/sample/discordBot_101.html))
1. 環境変数 `TEXT_CHAT_LOGGER_TOKEN` に Token を登録する
1. [Google Spread Sheets の Java Quickstart](https://developers.google.com/sheets/api/quickstart/java) のページで、 `credentials.json` を取得し、`DiscordTextLogger.jar` と同じディレクトリに配置する。詳細な手順は下記
1. [Google Spread Sheets](https://docs.google.com/spreadsheets/u/0/) にアクセスし、空シートを1つ作成し、その ID を取得する
1. 環境変数 `SHEET_ID` に空シートの ID を登録する
1. `DiscordTextLogger.jar` を実行する。コマンドプロンプトから `java -jar DiscordTextLogger.jar`
1. 初めての起動の際に `Please open the following address in your browser: https://accounts.google.com/o/oauth2/auth?access_type=...` と URL が表示されるのでこの URL にアクセスする
1. URL にアクセスすると Google のログイン画面が表示されるのでログインする
1. **このアプリは確認されていません** という警告画面が出るので、画面下の方にある「詳細」をクリックし表示される「Quickstart（安全ではないページ）に移動」をクリックする
1. **Quickstart への権限の付与** という画面が出てくるので「許可」をクリックする
1. **選択内容を確認してください** という画面が出てくるので「許可」をクリックする

以上の手順で　Bot が発言を収集し始める。
`DiscordTextLogger.jar`が実行されている間は Bot が見つけた発言は全てスプレッドシートに記録される。

### Google Spread Sheets の credentials.json 取得手順

1. [Google Spread Sheets の Java Quickstart](https://developers.google.com/sheets/api/quickstart/java) にアクセスする
2. `Enable the Google Sheets API` をクリックする
3. **Configure your OAuth client** という画面が出てくるので `Desktop app` を選択し、CREATE をクリックする
4. **You're all set!** という画面が出てくるので `DOWNLOAD CLIENT CONFIGURATION` をクリックし、 `credentials.json` をダウンロードする

### Google Spread Sheets で作ったシートの ID

シートのURL は `https://docs.google.com/spreadsheets/d/${ID}/`という構造になっています。この `${ID}` がシートの ID となる。

例えばシートのアドレスが `https://docs.google.com/spreadsheets/d/7DQ-EnMD9YtLbd7Tjky5hke_6Hv19K1vuoeR-Re7HsBw/` であれば   
`7DQ-EnMD9YtLbd7Tjky5hke_6Hv19K1vuoeR-Re7HsBw` が ID。

## 自前でコンパイルする

`$ mvn clean compile package` して生成された `/target/discord-textChatLogger-jar-with-dependencies.jar` を `DiscordTextLogger.jar` とリネームして使う
