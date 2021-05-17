from pymongo import MongoClient
from flask import Flask, request, session, g, redirect, url_for, abort, render_template, flash, jsonify
from getChartData import getDayChartData, getRealTimeChartData
import json

# configuration
DEBUG = False
SECRET_KEY = 'development key'
client = None

# create our little application
app = Flask(__name__)
app.config.from_object(__name__)
realTimeChartObj = getRealTimeChartData()
dayChartObj = getDayChartData()

@app.before_request
def before_request():
    # g.client = MongoClient("localhost", 27017)
    g.client = MongoClient("mongodb+srv://yoo:789retry@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")

@app.teardown_request
def teardown_request(exception):
    g.client.close()

@app.route('/login', methods=['POST'])
def login():
    success = True
    message = "로그인에 성공했습니다."
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId" : request.form['userId']})

        if user:
            if user['password'] == request.form['password']:
                return jsonify({'success': success, 'message': message}), 200
            else:
                success = False
                message = "비밀번호가 일치하지 않습니다."
        else:
            success = False
            message = "일치하는 아이디가 없습니다."
    else:
        return 400

    return jsonify({'success': success, 'message': message}), 200

@app.route('/signup', methods=['POST'])
def signup():
    success = True
    message = "회원가입에 성공했습니다."
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId" : request.form['userId']})

        if user:
            success = False
            message = "중복되는 아이디가 존재합니다."
        else:
            data = {"userId": request.form['userId'], "password": request.form['password'], "creonAccount": request.form['creonAccount'], 'active': False}
            g.client.Project.userData.insert_one(data)

    return jsonify({'success': success, 'message': message}), 200

@app.route('/home', methods=['POST'])
def home():
    success = False
    message = '유저가 존재하지 않습니다.'
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId" : request.form['userId']})
        if user:
            user['own']['currentMoney'] = 0
            user['own']['currentDiff'] = 0
            for stock in user['own']['stocks']:
                user['own']['stocks'][stock]['diff'] = user['own']['stocks'][stock]['price'] - realTimeChartObj.datas[stock]['price']
                user['own']['currentMoney'] += realTimeChartObj.datas[stock]['price']
                user['own']['currentDiff'] += user['own']['stocks'][stock]['diff'] * user['own']['stocks'][stock]['size']
            success = True
            message = user['own']
    return jsonify({'success': success, 'message': message}), 200

@app.route('/info', methods=['POST'])
def info():
    success = False
    message = '기업이 존재하지 않습니다.'
    if request.method == 'POST':
        if dayChartObj.datas[request.form['companyName']]:
            success = True
            message = {'price': realTimeChartObj.datas[request.form['companyName']]['price'],
                       'diff': realTimeChartObj.datas[request.form['companyName']]['diff'],
                       'chartData': dayChartObj.datas[request.form['companyName']]}
            db = g.client.Project
            news = db.newsData.find({'회사명': realTimeChartObj.datas['companyName']}).limit(20)
            message['newsData'] = [{'기사제목': n['기사제목'], '언론사':n['언론사'], '날짜':n['날짜'], '링크':n['링크']} for n in news]
    return jsonify({'success': success, 'message': message}), 200

@app.route('/myInfo', methods=['POST'])
def myInfo():
    success = False
    message = '유저가 존재하지 않습니다.'
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId": request.form['userId']})
        if user:
            success = True
            user['own']['currentMoney'] = 0
            user['own']['currentDiff'] = 0
            for stock in user['own']['stocks']:
                user['own']['currentMoney'] += realTimeChartObj.datas[stock]['price']
                user['own']['currentDiff'] += (user['own']['stocks'][stock]['price'] - realTimeChartObj.datas[stock]['price']) * user['own']['stocks'][stock][
                    'size']
            message = user['own']
            message['history'] = [h for h in user['history'][:30]]

    return jsonify({'success': success, 'message': message}), 200

if __name__ == '__main__':
    realTimeChartObj.run()
    dayChartObj.getOldDatas(30) # 최근 30일
    dayChartObj.run()
    # client = MongoClient("mongodb+srv://choi:zeKf2E10mHYA9Ivu@cluster0.pidsj.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
    # db = client.Project
    app.run(debug=DEBUG)