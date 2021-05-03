from flask import Flask, request, jsonify
from flask import session
from flask_jwt_extended import *
from pymongo import MongoClient
app = Flask(__name__)
# JWT 매니저 활성화
app.config.update(DEBUG = True, JWT_SECRET_KEY = "thisissecertkey" )
                  # 정보를 줄 수 있는 과정도 필요함 == 토큰에서 유저 정보를 받음

jwt = JWTManager(app)

# JWT 쿠키 저장
app.config['JWT_COOKIE_SECURE'] = False # https를 통해서만 cookie가 갈 수 있는지 (production 에선 True)
app.config['JWT_TOKEN_LOCATION'] = ['cookies']
app.config['JWT_ACCESS_COOKIE_PATH'] = '/' # access cookie를 보관할 url (Frontend 기준)
app.config['JWT_REFRESH_COOKIE_PATH'] = '/' # refresh cookie를 보관할 url (Frontend 기준)
# CSRF 토큰 역시 생성해서 쿠키에 저장할지
# (이 경우엔 프론트에서 접근해야하기 때문에 httponly가 아님)
app.config['JWT_COOKIE_CSRF_PROTECT'] = True

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

    access_token = create_access_token(identity=userid, expires_delta=False)
    refresh_token = create_refresh_token(identity=userid)
    resp = jsonify({'login':True})
    set_access_cookies(resp, access_token)
    set_refresh_cookies(resp, refresh_token)
    print('access token', access_token)
    print('refresh token', refresh_token)
    return resp

if __name__ == '__main__':
    app.run()