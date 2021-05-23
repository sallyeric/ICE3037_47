from pymongo import MongoClient
client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
db = client.Project
user = db.userData.find()
for u in user:
    print(u)
    if u['active'].get('SK하이닉스'):
        print(u['userId'], '잇음')
    else:
        print(u['userId'], '없음')