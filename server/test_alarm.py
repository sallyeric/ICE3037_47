from FCM import FCM
from pymongo import MongoClient

client = MongoClient(
    "mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
db = client.Project
users = db.userData.find()
for user in users:
    print("token값: ", user['token'])
    FCM.sendMessage("매도", "{}원에 자동으로 {}을(를) 매도했습니다".format(10, "NAVER"), user['token'])