#!/usr/local/bin/python
import json
import time
import html
import os
import sys
import logging
import zipfile
import urllib.request
import myconfiguration

maxIteration = myconfiguration.MAX_LOG_GET_TIMES

def postWebhook(applicationId, token, channel):
    url = f'https://discordapp.com/api/webhooks/{applicationId}/{token}'
    logging.error(url)
    headers = {
        'Content-Type': 'application/json',
        'User-Agent': 'DiscordBot (https://github.com/Shunshun94/discord-textChatLogger, 10)'
    }
    data = {
        'content': f'[ダウンロード]({myconfiguration.DOWNLOAD_PATH}{channel}.zip) このダウンロードリンクは明日以降どこかのタイミングで利用できなくなるので今のうちにダウンロードしてください',
    }
    request = urllib.request.Request(url, json.dumps(data).encode(), headers, method='POST')
    try:
        with urllib.request.urlopen(request) as res:
            logging.error(res.read())
    except urllib.error.HTTPError as err:
        logging.error(err)
    except urllib.error.URLError as err:
        logging.error(err)

def getTopLine():
    result = ""
    with open("requestsList", "r+") as fp:
        lines = fp.readlines()
        if len(lines) > 0:
            result = lines[0]
            fp.seek(0)
            fp.truncate()
            fp.writelines(lines[1:])
    return result

def getLog(channel, before, iterationCount):
    logging.error(f'log get {channel} / before = {before} / iteration = {iterationCount + 1}/{maxIteration} ')
    suffix = f'?before={before}&limit=100' if before else '?limit=100'
    url = f'https://discordapp.com/api/channels/{channel}/messages{suffix}'
    headers = {
        'Authorization': f'Bot {myconfiguration.ACCESS_TOKEN}',
        'User-Agent': 'DiscordBot (https://github.com/Shunshun94/discord-textChatLogger, 10)'
    }
    req = urllib.request.Request(url, headers = headers)
    with urllib.request.urlopen(req) as res:
        body = res.read()
    return json.loads(body)

def logJsonToHtml(json):
    return """
    <p>
    <span></span>
    <span>{username}</span>
    <span>{content}</span>
    </p>
    """.format( username = html.escape(json["author"]["username"]), content = "<br/>".join(list(map(html.escape, json["content"].split('\n')))) )

def getLogs(channel):
    before = 0
    lastLength = 1
    result = []
    iterationCount = 0
    while (lastLength and iterationCount < maxIteration):
        tmp = getLog(channel, before, iterationCount)
        result.extend(tmp)
        lastLength = len(tmp)
        if lastLength > 0:
            before = tmp[-1]['id']
            iterationCount += 1
            time.sleep(3)
    result.reverse()
    return "\n".join(list(map(logJsonToHtml, result)))

def writeFile(channel, text):
    # 8cd4589aec7122ffd860b4880f6613ba224f03fa のようにするともっと単純に書けるが、
    # そうすると windows でうまく unzip できないのでこっちの書き方
    with open(f'./{channel}.html', mode='w') as f:
        f.write(text)
    with zipfile.ZipFile(f'{channel}.zip', mode='w', compression=zipfile.ZIP_DEFLATED, strict_timestamps=False) as zf:
        zf.write(f'./{channel}.html')
    os.remove(f'./{channel}.html')

logging.error('start to parse requests')
targets = getTopLine()
if targets == "":
    print("Status: 200 OK")
    print("Content-Type: text/html")
    print()
    print("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /></head><body><p>中身がなにもないよ</p></body></html>\n")
    sys.exit(0)
target = targets.split(',')
server = target[0]
channel = target[1]
requestToken = target[2]
applicationId = myconfiguration.APPLICATION_ID


writeFile(channel, f'<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"https://shunshun94.github.io/shared/jquery/io/github/shunshun94/trpg/logEditor/resources/default.css\" type=\"text/css\"><meta charset=\"UTF-8\" /></head><body>{getLogs(channel)}</body></html>')

postWebhook(applicationId, requestToken, channel)

print("Status: 200 OK")
print("Content-Type: text/html\n")
print()
