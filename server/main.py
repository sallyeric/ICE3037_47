from pymongo import MongoClient
from flask import Flask, request, session, g, redirect, url_for, abort, render_template, flash, jsonify
import json

# configuration
DEBUG = False
SECRET_KEY = 'development key'
client = None

# create our little application
app = Flask(__name__)
app.config.from_object(__name__)

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
            data = {"userId": request.form['userId'], "password": request.form['password'], "creonAccount": request.form['creonAccount']}
            g.client.Project.userData.insert_one(data)

    return jsonify({'success': success, 'message': message}), 200

if __name__ == '__main__':
    app.run(debug=DEBUG)