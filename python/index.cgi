#!/usr/local/bin/python
import json
import os
import sys
import ed25519
import logging
import myconfiguration

def verify(signature, timestamp, body):
    try:
        ed25519.checkvalid(bytes.fromhex(signature), f"{timestamp}{body}".encode(), bytes.fromhex(myconfiguration.PUBLIC_KEY))
        return True
    except Exception:
        return False


try:
    logging.error('start to parse requests')
    
    ed25519Value = os.environ['HTTP_X_SIGNATURE_ED25519']
    timestamp = os.environ['HTTP_X_SIGNATURE_TIMESTAMP']
    rawBody = str.strip(sys.stdin.read())

    body = json.loads(rawBody)
    logging.error(body['type'])

    if not verify(ed25519Value, timestamp, rawBody):
        logging.error('Failed to validate ed25519 signature')
        print("Status: 401 Unautorized")
        print("Content-Type: text/html")
        print()
        htmlText = '''
        <!DOCTYPE html>
        <html>
        <head><meta charset="UTF-8" /></head>
        <body>
        <h1>認証失敗しました</h1>
        </body></html>
        '''
        print( htmlText.encode("UTF-8", 'ignore').decode('UTF-8') )
        sys.exit()
    
    
    if body['type'] == 1:
        print("Status: 200 OK")
        print("Content-Type: text/json")
        print()
        print("{\"type\": 1}")
    if body['type'] == 2:
        print("Status: 200 OK")
        print("Content-Type: application/json;charset=UTF-8")
        print()
        print("{\"type\":4,\"data\":{\"content\":\"ログを取得します。最大1時間程度お待ちください\"}}")
        with open('./requestsList', mode='a') as requestList:
            requestList.write(f'{body["guild_id"]},{body["channel_id"]},{body["token"]}\n')
        logging.error("return")

except KeyError as error:
    logging.error(error)
    print("Status: 401 Unautorized")
    print("Content-Type: text/html")
    print()
    htmlText = '''
    <!DOCTYPE html>
    <html>
    <head><meta charset="UTF-8" /></head>
    <body>
    <h1>
    '''
    htmlText += myconfiguration.BROWSER_ACCESS_HEADER
    htmlText += '''
    </h1>
    <p>
    '''
    htmlText += myconfiguration.BROWSER_ACCESS_TEXT
    htmlText += '''
    </p>
    </body></html>
    '''
    print( htmlText.encode("UTF-8", 'ignore').decode('UTF-8') )
