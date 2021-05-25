from pymongo import MongoClient
from flask import Flask, request, session, g, redirect, url_for, abort, render_template, flash, jsonify
from getChartData import getDayChartData, getRealTimeChartData
from getMACD import getRealTimeMACD
from creonTrade import creonTrade
from newsCrawl import newsCrawl
from Volatility import Volatility
import json

# configuration
DEBUG = False
SECRET_KEY = 'development key'
client = None

# create our little application
app = Flask(__name__)
app.config.from_object(__name__)

codes = ['A000270', 'A000660', 'A005380', 'A005490', 'A005930', 'A035420', 'A035720', 'A051910', 'A068270']
names = ['기아', 'SK하이닉스', '현대차', 'POSCO', '삼성전자', 'NAVER', '카카오', 'LG화학', '셀트리온']
codeToName = {}
nameToCode = {}
for i in range(9):
    codeToName[codes[i]] = names[i]
    nameToCode[names[i]] = codes[i]

creonTradeObj = creonTrade()
realTimeChartObj = getRealTimeChartData()
realTimeMACDObj = getRealTimeMACD(creonTradeObj)
realTimeVolatility = Volatility(creonTradeObj)
dayChartObj = getDayChartData()
newsCrawlObj = newsCrawl()
print('init complete')


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
                db.userData.update_one({'userId':request.form['userId']}, {'$set':{'token':request.form['token']}})
                return jsonify({'success': success, 'message': message}), 200
            else:
                success = False
                message = "비밀번호가 일치하지 않습니다."
        else:
            success = False
            message = "일치하는 아이디가 없습니다."
    else:
        return 400
    print('login')
    print(success, message)
    print(jsonify({'success': success, 'message': message}))
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
            data = {"userId": request.form['userId'],
                    "password": request.form['password'],
                    "creonAccount": request.form['creonAccount'],
                    'active':{'money':100000},
                    'own':{'stocks':{}},
                    'history':[],
                    'active':{'money':0}}
            g.client.Project.userData.insert_one(data)
    print('signup')
    print(success, message)
    print(jsonify({'success': success, 'message': message}))
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
                user['own']['stocks'][stock]['diff'] = realTimeChartObj.datas[stock]['price'] - user['own']['stocks'][stock]['price']
                user['own']['stocks'][stock]['currentPrice'] = realTimeChartObj.datas[stock]['price']
                user['own']['currentMoney'] += realTimeChartObj.datas[stock]['price']
            for stock in user['active']:
                user['own']['currentMoney'] += user['active'][stock]['current']
                user['own']['currentDiff'] += user['active'][stock]['origin']
            user['own']['currentDiff'] = user['own']['currentMoney'] - user['own']['currentDiff']
            success = True
            message = user['own']
    print('home')
    print(success, message)
    print(jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}))
    return jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}), 200

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
            news = db.newsData.find({'회사명': request.form['companyName']}).limit(20)
            message['newsData'] = [{'기사제목': n['기사제목'], '언론사':n['언론사'], '날짜':n['날짜'], '링크':n['링크']} for n in news]
    print('info')
    print(success, message)
    return jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}), 200

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
                user['own']['stocks'][stock]['diff'] = user['own']['stocks'][stock]['price'] - \
                                                       realTimeChartObj.datas[stock]['price']
                user['own']['stocks'][stock]['currentPrice'] = realTimeChartObj.datas[stock]['price']
                user['own']['currentMoney'] += realTimeChartObj.datas[stock]['price']
            for stock in user['active']:
                user['own']['currentMoney'] += user['active'][stock]['current']
                user['own']['currentDiff'] += user['active'][stock]['origin']
            user['own']['currentDiff'] = user['own']['currentMoney'] - user['own']['currentDiff']
            message = user['own']
            message['history'] = [h for h in user['history'][:30]]
    print('myInfo')
    print(success, message)
    print(jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}))
    return jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}), 200

@app.route('/OnAutoTrade', methods=['POST'])
def OnAutoTrade():
    success = False
    message = '유저가 존재하지 않습니다.'
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId": request.form['userId']})
        if user:
            success = True
            message = "성공"
            db.userData.update_one({'userId':request.form['userId']}, {'$set':{{request.form['companyName'] : {'origin': request.form['budgets'],
                                                                                                               'current': request.form['budgets'],
                                                                                                               'macd': request.form['check1'],
                                                                                                               'volat': request.form['check2'],
                                                                                                               'lstm': request.form['check3']}}}})
    print('OnAutoTrade')
    print(success, message)
    print(jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}))
    return jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}), 200

@app.route('/OffAutoTrade', methods=['POST'])
def OffAutoTrade():
    success = False
    message = '유저가 존재하지 않습니다.'
    if request.method == 'POST':
        db = g.client.Project
        user = db.userData.find_one({"userId": request.form['userId']})
        if user:
            success = True
            message = "성공"
            creonTradeObj.sellOrder(nameToCode[request.form['companyName']], request.form['userId'])

    print('OnAutoTrade')
    print(success, message)
    print(jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}))
    return jsonify({'success': success, 'message': json.dumps(message, ensure_ascii=False)}), 200

if __name__ == '__main__':
    realTimeChartObj.run()
    dayChartObj.run()
    newsCrawlObj.start()
    realTimeVolatility.start()

    print('flask ready to run')
    app.run(debug=DEBUG, host='0.0.0.0', port=5000)