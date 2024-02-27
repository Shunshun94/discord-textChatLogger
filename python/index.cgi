#!/usr/local/bin/python
import cgi
import os
import sys
sys.path.append('./pynacl/src')

from nacl.signing import VerifyKey

ed55519 = os.environ['HTTP_X-SIGNATURE-ED25519']
timestamp = os.environ['HTTP_X-SIGNATURE-TIMESTAMP']
body = cgi.FieldStorage()

print("Content-Type: text/html")
print()
htmlText = '''
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8" /></head>
<body>
<h1>つくってます</h1>
'''

htmlText += '''
</body>
</html>
'''
print( htmlText.encode("UTF-8", 'ignore').decode('UTF-8') )
