# 設定ファイル

[myconfiguration.default.py](./myconfiguration.default.py) を編集して使います。
PUBLIC_KEY の情報を入れれば十分です。サーバにアップロードする際は `myconfiguration.py` にリネームしてください。

# ed25519.py の入手元

[公式](https://ed25519.cr.yp.to/software.html)の参照実装は python2 向けなので、修正が必要でした。
[こちらの QA](https://monero.stackexchange.com/questions/9820/recursionerror-in-ed25519-py)を参考に修正版を取得してそれを使っています。
[DL元](https://github.com/bigreddmachine/MoneroPy/blob/98e7feb20bf8595e6a0d0dda06c73517f5bb3ad4/moneropy/crypto/ed25519.py)

# コマンドの登録

[registerCommand.bat](./registerCommand.bat) を実行します。第一引数に bot の token、第二引数にアプリケーションの ID を入れる必要があります

# 挙動
1. スラッシュコマンドを受ける
1. [index.cgi](./index.cgi)が最初の返答をする
1. スラッシュコマンドを受けた際に受け取ったサーバ情報・チャネル情報に基づいてログを生成する
1. ログをスラッシュコマンドに返答する


