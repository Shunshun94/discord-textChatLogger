#!/usr/local/bin/python
import json
import logging
import urllib.request
import myconfiguration

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

def getLog(channel):
    url = f'https://discordapp.com/api/channels/{channel}/messages'
    headers = {
        'Authorization': f'Bot {myconfiguration.ACCESS_TOKEN}',
        'User-Agent': 'DiscordBot (https://github.com/Shunshun94/discord-textChatLogger, 10)'
    }
    req = urllib.request.Request(url, headers = headers)
    with urllib.request.urlopen(req) as res:
        body = res.read()
    return body


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
print(f'<p>{getLog(channel)}</p>\n')
