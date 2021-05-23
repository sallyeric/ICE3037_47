from pyfcm import FCMNotification
from pymongo import MongoClient
from flask import Flask, request, session, g, redirect, url_for, abort, render_template, flash, jsonify

APIKEY = "AAAA0hFni2E:APA91bECv3DRFCE0LyhEST8Jtbj6AJH7_gfZN3CMtFjXtpdkeMgipdxLhlSXVENljGJS9_PIYC0gmLxKwF49dJqnpWhzxPmQLBCeIsuu62JfBXdrwCZVP9lZm8y1bmO_HVZtF20Yo3xd"
TOKEN = "c12uijx_bAc:APA91bEmATm07LunBAnl8dWJ7lXvgOiKikBDhl9gsXkKteEoW_NcD2ZG0btjv_MUiqZ_SeyO6GeAeWx6RzO8mvpMt_LnThWaEkdbFpTDzEeLYOd0sFuyGBMf7wljfIZ1GApdi3Iut3Gd"
# 파이어베이스 콘솔에서 얻어 온 서버 키를 넣어 줌

# configuration
DEBUG = False
SECRET_KEY = 'development key'
client = None

app = Flask(__name__)
app.config.from_object(__name__)
push_service = FCMNotification(APIKEY)

@app.before_request
def before_request():
    # g.client = MongoClient("localhost", 27017)
    g.client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")

@app.teardown_request
def teardown_request(exception):
    g.client.close()


def sendMessage(body, title):
    # 메시지 (data 타입)
    data_message = {
        "body": body,
        "title": title
    }
    # 토큰값을 이용해 1명에게 푸시알림을 전송함
    result = push_service.single_device_data_message(registration_id=TOKEN, data_message=data_message)
    # 전송 결과 출력
    print(result)

sendMessage("배달의 민족", "치킨 8000원 쿠폰 도착!")