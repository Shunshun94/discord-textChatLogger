#!/usr/local/bin/python
import json
import time
import html
import logging
import urllib.request
import myconfiguration

maxIteration = myconfiguration.MAX_LOG_GET_TIMES

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

logging.error('start to parse requests')
targets = getTopLine()
if targets == "":
    print("Status: 200 OK")
    print("Content-Type: text/html")
    print()
    print("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /></head><body><p>中身がなにもないよ</p></body></html>\n")
target = targets.split(',')
server = target[0]
channel = target[1]
requestToken = target[2]

print("Status: 200 OK")
print("Content-Type: text/html")
print()
print(f'<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"https://shunshun94.github.io/shared/jquery/io/github/shunshun94/trpg/logEditor/resources/default.css\" type=\"text/css\"><meta charset=\"UTF-8\" /></head><body>{getLogs(channel)}</body></html>')
