# Rep
# Android backend copy
# ...existing code from original...
from flask import Blueprint, request, jsonify, session, make_response
from app import db
from app.models.People_Models.user import User
from app.models.People_Models.PasswordUpdater import PasswordUpdater
from app.utils.user_utils import manage_user_row, mark_all_activities_as_read
from app.utils.mail_utils import send_mail
import hashlib
import os
import jwt
import datetime
import time

user_bp = Blueprint('login_user', __name__)

@user_bp.route('/login', methods=['POST'])
@user_bp.route('/api/user/login', methods=['POST'])
def api_login_user():
    # Try to get JSON data first
    data = request.get_json(silent=True)
    if data and 'username' in data and 'password' in data:
        username = data['username']
        password = data['password']
    else:
        # Fallback to form data
        username = request.form.get('username')
        password = request.form.get('password')
        if not username or not password:
            return jsonify({'error': 'Missing username or password'}), 400

    user = User.query.filter_by(username=username).first()
    if user and user.password == password:
        # You should use hashed passwords in production!
        token = jwt.encode({'user_id': user.id}, 'your-secret-key', algorithm='HS256')
        return jsonify({'result': {'message': 'Login successful', 'id': user.id, 'token': token}}), 200
    else:
        return jsonify({'error': 'Invalid credentials'}), 401

@user_bp.route('/logout', methods=['POST'])
def api_logout_user():
    return jsonify({'message': 'Logout successful'}), 200

@user_bp.route('/forgot_password', methods=['POST'])
def api_forgot_password():
    return jsonify({'message': 'Forgot password endpoint not implemented'}), 501
