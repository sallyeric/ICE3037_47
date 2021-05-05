from flask import Flask, request, jsonify
from flask import session
from pymongo import MongoClient
from getChartData import getRealTimeChartData
from getChartData import getDayChartData

GetRealTimeChart = getRealTimeChartData()
GetDayChart = getDayChartData()
app = Flask(__name__)
# JWT 매니저 활성화
client = MongoClient('localhost', 27017)
db = client.db
db.users.insert_one({'user_id': 'test',
                     'user_pwd': 'test'})
@app.route('/')
def hello_world():
    return 'Hello World!'

@app.route('/register', methods=['POST'])
def register():
    #if request.method =='POST':
    userid = request.form['userid']
    password = request.form['password']
    userinfo = {'user_id':userid,
                'user_pwd':password}
    db.users.insert_one(userinfo)
    return {"message":"register complete"}

@app.route('/login', methods=['POST'])
def login():
    #if request.method =='POST':
    userid = request.form['userid']
    password = request.form['password']
    userinfo = {'user_id':userid,
                'user_pwd':password}
    user = db.users.find_one(userinfo)
    if user is None:
        return jsonify({'login' : False})
    resp = jsonify({'login':True})
    return resp

if __name__ == '__main__':
    GetRealTimeChart.run()
    GetDayChart.run()
    app.run()
