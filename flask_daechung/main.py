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
    g.client = MongoClient("localhost", 27017)

@app.teardown_request
def teardown_request(exception):
    g.client.close()

@app.route('/')
def show_entries():
    db = g.client.UserData
    collection = db.mongoTest
    results = collection.find()
    return render_template('show_entries.html', entries=results)

@app.route('/add', methods=['POST'])
def add_entry():
    if not session.get('logged_in'):
        abort(401)
    data = {"name": request.form['title'], "content" : request.form['text']}
    g.client.UserData.mongoTest.insert_one(data)
    flash('New entry was successfully posted')
    return redirect(url_for('show_entries'))

@app.route('/delete', methods=['POST'])
def delete_entry():
    if not session.get('logged_in'):
        abort(401)
    data = {"name": request.form['title']}
    g.client.UserData.mongoTest.delete_one(data)
    flash('Entry was successfully deleted')
    return redirect(url_for('show_entries'))

@app.route('/user/<username>')
def show_user_profile(username):
    return 'User %s' % username

@app.route('/post/<int:post_id>')
def show_post(post_id):
    return 'Post %d' % post_id

@app.route('/mongo')
def mongoTest():
    db = g.client.UserData
    collection = db.mongoTest
    results = collection.find()
    return render_template('mongo.html', data=results)

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        db = g.client.UserData
        user = db.userData.find_one({"id" : request.form['username']})

        if user:
            if user['pw'] == request.form['password']:
                session['logged_in'] = True
                flash('You were logged in')
                return redirect(url_for('show_entries'))
            else:
                error = 'Invalid password'
        else:
            error = 'Invalid username'

    return render_template('login.html', error=error)

@app.route('/signup', methods=['GET', 'POST'])
def signup():
    error = None
    if request.method == 'POST':
        db = g.client.UserData
        user = db.userData.find_one({"id" : request.form['id']})

        if user:
            error = 'Invalid username'
        else:
            if request.form['pw'] == request.form['pw2']:
                data = {"id": request.form['id'], "pw": request.form['pw']}
                g.client.UserData.userData.insert_one(data)
                flash('You were sign up successfully')
                return render_template('login.html')
            else:
                error = 'Password is not coincide'

    return render_template('signup.html', error=error)

@app.route('/logout')
def logout():
    session.pop('logged_in', None)
    flash('You were logged out')
    return redirect(url_for('show_entries'))

if __name__ == '__main__':
    app.run(debug=DEBUG)