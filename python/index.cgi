#!/usr/local/bin/python
import json
import os
import sys
import ed25519
import logging

def verify(signature, timestamp, body):
    return False


try:
    logging.error('start to parse requests')
    
    ed25519Value = os.environ['HTTP_X_SIGNATURE_ED25519']
    timestamp = os.environ['HTTP_X_SIGNATURE_TIMESTAMP']
    rawBody = str.strip(sys.stdin.read())

    body = json.loads(rawBody)
    logging.error(body['type'])

    if not verify(ed25519Value, timestamp, rawBody):
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
    <h1>Discord app によるアクセスを受け付けるようにここは作られています</h1>
    <p>なのでブラウザでアクセスしても何も見せられません＞＜</p>
    </body></html>
    '''
    print( htmlText.encode("UTF-8", 'ignore').decode('UTF-8') )
