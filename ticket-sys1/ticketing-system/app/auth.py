from flask import Blueprint, request, jsonify
from database import get_db

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/register', methods=['POST'])
def register():
    db = get_db()
    data = request.json
    db.cursor().execute("INSERT INTO users (username, password) VALUES (%s, %s)", (data['username'], data['password']))
    db.commit()
    return jsonify({"msg": "registered"}), 201

@auth_bp.route('/login', methods=['POST'])
def login():
    db = get_db()
    data = request.json
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM users WHERE username=%s AND password=%s", (data['username'], data['password']))
    user = cursor.fetchone()
    if user:
        return jsonify({"msg": "login success", "uid": user['id']})
    else:
        return jsonify({"msg": "invalid credentials"}), 401
