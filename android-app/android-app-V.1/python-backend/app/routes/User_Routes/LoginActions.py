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
from werkzeug.security import check_password_hash

user_bp = Blueprint('login_user', __name__)

@user_bp.route('/login', methods=['POST'])
@user_bp.route('/api/user/login', methods=['POST'])
def api_login_user():
    # Accept both JSON and form data; support email or username
    data = request.get_json(silent=True) or {}
    email = (data.get('email') or '').strip() if isinstance(data, dict) else ''
    username = (data.get('username') or '').strip() if isinstance(data, dict) else ''
    password = (data.get('password') if isinstance(data, dict) else None)

    # Fallback to form fields if JSON missing
    if not (email or username) or not password:
        form = request.form
        email = (email or form.get('email') or '').strip()
        username = (username or form.get('username') or '').strip()
        password = password or form.get('password')

    # Validate presence
    if not ((email or username) and password):
        return jsonify({'error': 'Missing email/username or password'}), 400

    # Lookup by email first if provided, else by username
    user = None
    if email:
        user = User.query.filter_by(email=email).first()
    if not user and username:
        user = User.query.filter_by(username=username).first()

    if not user or not user.password:
        return jsonify({'error': 'Invalid credentials'}), 401

    # Verify password
    verified = False
    try:
        # Handles werkzeug generate_password_hash (pbkdf2:sha256 by default)
        verified = check_password_hash(user.password, password)
    except Exception:
        # If stored password is not a werkzeug hash, skip to fallbacks
        verified = False

    if not verified:
        # Fallback: legacy MD5(salt+password) support if PASS_SALT set and stored looks like MD5
        salt = os.environ.get('PASS_SALT', '')
        if salt and len(user.password) == 32:
            expected_md5 = hashlib.md5((salt + password).encode()).hexdigest()
            verified = (user.password == expected_md5)

    if not verified:
        # Optional: allow plaintext match only if explicitly enabled (for old test data)
        if os.environ.get('ALLOW_PLAINTEXT_LOGIN', '0') == '1' and user.password == password:
            verified = True

    if not verified:
        return jsonify({'error': 'Invalid credentials'}), 401

    # Auth OK: issue JWT and return user payload
    token = jwt.encode({'user_id': user.id}, 'your-secret-key', algorithm='HS256')
    user_data = {
        "id": user.id,
        "email": user.email,
        "username": user.username,
        "fname": user.fname,
        "lname": user.lname
        # Add other fields as needed
    }
    return jsonify({"result": user_data, "token": token}), 200

@user_bp.route('/logout', methods=['POST'])
def api_logout_user():
    return jsonify({'message': 'Logout successful'}), 200

@user_bp.route('/forgot_password', methods=['POST'])
def api_forgot_password():
    return jsonify({'message': 'Forgot password endpoint not implemented'}), 501
