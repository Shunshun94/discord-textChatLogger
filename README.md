# Discord Text Logger

Discord のテキストチャットを Google Spread Sheet にリアルタイムで保存するツール。   
サーバ管理者はは削除された発言であっても後から何が書き込まれたのかを確認でき、サーバ内で起こったイベントを追跡できる。

## どうやって使うの

1. [DiscordTextLogger.jar](https://drive.google.com/open?id=13yeQ_CW6vtuuyOY1DaxgY9k5Vifbt1fR) をダウンロードする
1. Discord Bot を作り Token を取得する ([Discord の Bot を作ろう！](https://shunshun94.github.io/shared/sample/discordBot_101.html))
1. 環境変数 `TEXT_CHAT_LOGGER_TOKEN` に Token を登録する
1. `credentials.json` を取得し、`DiscordTextLogger.jar` と同じディレクトリに配置する。詳細な手順は下記
1. [Google Spread Sheets](https://docs.google.com/spreadsheets/u/0/) にアクセスし、空シートを1つ作成し、その ID を取得する
1. 環境変数 `SHEET_ID` に空シートの ID を登録する
1. `DiscordTextLogger.jar` を実行する。コマンドプロンプトから `java -jar DiscordTextLogger.jar`
1. 初めての起動の際に `Please open the following address in your browser: https://accounts.google.com/o/oauth2/auth?access_type=...` と URL が表示されるのでこの URL にアクセスする
1. URL にアクセスすると Google のログイン画面が表示されるのでログインする
1. (楽な手順で credentials.json を取得した場合のみ) **このアプリは確認されていません** という警告画面が出るので、画面下の方にある「詳細」をクリックし表示される「Quickstart（安全ではないページ）に移動」をクリックする
1. **[アプリ名] への権限の付与** という画面が出てくるので「許可」をクリックする。楽な手段で credentials.json を取得した場合、アプリ名は Quickstart となる
1. **選択内容を確認してください** という画面が出てくるので「許可」をクリックする

以上の手順で　Bot が発言を収集し始める。
`DiscordTextLogger.jar`が実行されている間は Bot が見つけた発言は全てスプレッドシートに記録される。

次回以降は `java -jar DiscordTextLogger.jar` を実行するのみで良い。

### Google Spread Sheets の credentials.json 取得手順

#### 楽な方

1. [Google Spread Sheets の Java Quickstart](https://developers.google.com/sheets/api/quickstart/java) にアクセスする
2. `Enable the Google Sheets API` をクリックする
3. **Configure your OAuth client** という画面が出てくるので `Desktop app` を選択し、CREATE をクリックする
4. **You're all set!** という画面が出てくるので `DOWNLOAD CLIENT CONFIGURATION` をクリックし、 `credentials.json` をダウンロードする

#### 標準的な方

1. [Google Developers Console](https://console.developers.google.com/) にアクセスし、プロジェクトを新規作成
1. **認証情報** > **認証情報を作成** > **OAuth クライアント ID** をクリック
1. アプリケーションの種類を **デスクトップアプリ** に設定、名前を任意に設定し、作成
1. 認証情報の一覧画面から先に作った OAuth 2.0 クライアント ID を探し、右端のダウンロードボタンをクリックし `credentials.json` とリネームして保存  

### Google Spread Sheets で作ったシートの ID

シートのURL は `https://docs.google.com/spreadsheets/d/${ID}/`という構造になっています。この `${ID}` がシートの ID となる。

例えばシートのアドレスが `https://docs.google.com/spreadsheets/d/7DQ-EnMD9YtLbd7Tjky5hke_6Hv19K1vuoeR-Re7HsBw/` であれば   
`7DQ-EnMD9YtLbd7Tjky5hke_6Hv19K1vuoeR-Re7HsBw` が ID。

## 自前でコンパイルする

`$ mvn clean compile package` して生成された `/target/discord-textChatLogger-jar-with-dependencies.jar` を `DiscordTextLogger.jar` とリネームして使う

## ソースコード

[https://github.com/Shunshun94/discord-textChatLogger](https://github.com/Shunshun94/discord-textChatLogger)

## 利用しているライブラリ

`mvn license:add-third-party -D license.excludedScopes=test` コマンドによる自動生成

* (The Apache Software License, Version 2.0) Jackson-annotations (com.fasterxml.jackson.core:jackson-annotations:2.9.0 - http://github.com/FasterXML/jackson)
* (The Apache Software License, Version 2.0) Jackson-core (com.fasterxml.jackson.core:jackson-core:2.9.9 - https://github.com/FasterXML/jackson-core)
* (The Apache Software License, Version 2.0) jackson-databind (com.fasterxml.jackson.core:jackson-databind:2.9.3 - http://github.com/FasterXML/jackson)
* (The Apache Software License, Version 2.0) Google APIs Client Library for Java (com.google.api-client:google-api-client:1.30.4 - https://github.com/googleapis/google-api-java-client/google-api-client)
* (The Apache Software License, Version 2.0) Google Sheets API v4-rev581-1.25.0 (com.google.apis:google-api-services-sheets:v4-rev581-1.25.0 - http://nexus.sonatype.org/oss-repository-hosting.html/google-api-services-sheets)
* (The Apache Software License, Version 2.0) FindBugs-jsr305 (com.google.code.findbugs:jsr305:3.0.2 - http://findbugs.sourceforge.net/)
* (Apache 2.0) error-prone annotations (com.google.errorprone:error_prone_annotations:2.3.2 - http://nexus.sonatype.org/oss-repository-hosting.html/error_prone_parent/error_prone_annotations)
* (The Apache Software License, Version 2.0) Guava InternalFutureFailureAccess and InternalFutures (com.google.guava:failureaccess:1.0.1 - https://github.com/google/guava/failureaccess)
* (Apache License, Version 2.0) Guava: Google Core Libraries for Java (com.google.guava:guava:28.0-android - https://github.com/google/guava/guava)
* (The Apache Software License, Version 2.0) Guava ListenableFuture only (com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava - https://github.com/google/guava/listenablefuture)
* (The Apache Software License, Version 2.0) Google HTTP Client Library for Java (com.google.http-client:google-http-client:1.32.1 - https://github.com/googleapis/google-http-java-client/google-http-client)
* (The Apache Software License, Version 2.0) Jackson 2 extensions to the Google HTTP Client Library for Java. (com.google.http-client:google-http-client-jackson2:1.32.1 - https://github.com/googleapis/google-http-java-client/google-http-client-jackson2)
* (The Apache Software License, Version 2.0) J2ObjC Annotations (com.google.j2objc:j2objc-annotations:1.3 - https://github.com/google/j2objc/)
* (The Apache Software License, Version 2.0) Google OAuth Client Library for Java (com.google.oauth-client:google-oauth-client:1.30.3 - https://github.com/googleapis/google-oauth-java-client/google-oauth-client)
* (The Apache Software License, Version 2.0) Java 6 (and higher) extensions to the Google OAuth Client Library for Java. (com.google.oauth-client:google-oauth-client-java6:1.30.4 - https://github.com/googleapis/google-oauth-java-client/google-oauth-client-java6)
* (The Apache Software License, Version 2.0) Jetty extensions to the Google OAuth Client Library for Java. (com.google.oauth-client:google-oauth-client-jetty:1.30.4 - https://github.com/googleapis/google-oauth-java-client/google-oauth-client-jetty)
* (The Apache Software License, Version 2.0) com.neovisionaries:nv-websocket-client (com.neovisionaries:nv-websocket-client:2.6 - https://github.com/TakahikoKawasaki/nv-websocket-client)
* (Apache 2.0) OkHttp Logging Interceptor (com.squareup.okhttp3:logging-interceptor:3.9.1 - https://github.com/square/okhttp/logging-interceptor)
* (Apache 2.0) OkHttp (com.squareup.okhttp3:okhttp:3.9.1 - https://github.com/square/okhttp/okhttp)
* (Apache 2.0) Okio (com.squareup.okio:okio:1.13.0 - https://github.com/square/okio/okio)
* (Apache License, Version 2.0) Apache Commons Codec (commons-codec:commons-codec:1.11 - http://commons.apache.org/proper/commons-codec/)
* (The Apache Software License, Version 2.0) Apache Commons Logging (commons-logging:commons-logging:1.2 - http://commons.apache.org/proper/commons-logging/)
* (Apache 2.0) io.grpc:grpc-context (io.grpc:grpc-context:1.22.1 - https://github.com/grpc/grpc-java)
* (The Apache License, Version 2.0) OpenCensus (io.opencensus:opencensus-api:0.24.0 - https://github.com/census-instrumentation/opencensus-java)
* (The Apache License, Version 2.0) OpenCensus (io.opencensus:opencensus-contrib-http-util:0.24.0 - https://github.com/census-instrumentation/opencensus-java)
* (Apache License, Version 2.0) Apache HttpClient (org.apache.httpcomponents:httpclient:4.5.10 - http://hc.apache.org/httpcomponents-client)
* (Apache License, Version 2.0) Apache HttpCore (org.apache.httpcomponents:httpcore:4.4.12 - http://hc.apache.org/httpcomponents-core-ga)
* (Apache License, Version 2.0) Apache Log4j API (org.apache.logging.log4j:log4j-api:2.11.0 - https://logging.apache.org/log4j/2.x/log4j-api/)
* (GNU General Public License, version 2 (GPL2), with the classpath exception) (The MIT License) Checker Qual (org.checkerframework:checker-compat-qual:2.5.5 - https://checkerframework.org)
* (MIT license) Animal Sniffer Annotations (org.codehaus.mojo:animal-sniffer-annotations:1.17 - http://www.mojohaus.org/animal-sniffer/animal-sniffer-annotations)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty :: Continuation (org.eclipse.jetty:jetty-continuation:8.2.0.v20160908 - http://www.eclipse.org/jetty)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty :: Http Utility (org.eclipse.jetty:jetty-http:8.2.0.v20160908 - http://www.eclipse.org/jetty)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty :: IO Utility (org.eclipse.jetty:jetty-io:8.2.0.v20160908 - http://www.eclipse.org/jetty)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty :: Server Core (org.eclipse.jetty:jetty-server:8.2.0.v20160908 - http://www.eclipse.org/jetty)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty :: Utilities (org.eclipse.jetty:jetty-util:8.2.0.v20160908 - http://www.eclipse.org/jetty)
* (Apache Software License - Version 2.0) (Eclipse Public License - Version 1.0) Jetty Orbit :: Servlet API (org.eclipse.jetty.orbit:javax.servlet:3.0.0.v201112011016 - http://www.eclipse.org/jetty/jetty-orbit/javax.servlet)
* (Apache License, Version 2.0) Javacord (org.javacord:javacord:3.0.5 - https://www.javacord.org)
* (Apache License, Version 2.0) Javacord (api) (org.javacord:javacord-api:3.0.5 - https://www.javacord.org)
* (Apache License, Version 2.0) Javacord (core) (org.javacord:javacord-core:3.0.5 - https://www.javacord.org)
