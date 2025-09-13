# Rep
# User registration endpoint
from flask import Blueprint, request, jsonify
from app import db
from app.models.People_Models.user import User
from werkzeug.security import generate_password_hash
import jwt


register_bp = Blueprint('register_user', __name__)


# JSON registration (for legacy/testing)
@register_bp.route('/register', methods=['POST'])
def api_register_user():
    data = request.get_json()
    required_fields = ['email', 'username', 'password', 'fname', 'lname']
    for field in required_fields:
        if field not in data or not data[field]:
            return jsonify({'error': f'Missing field: {field}'}), 400

    if User.query.filter_by(email=data['email']).first():
        return jsonify({'error': 'Email already registered'}), 409
    if User.query.filter_by(username=data['username']).first():
        return jsonify({'error': 'Username already taken'}), 409

    hashed_password = generate_password_hash(data['password'])
    new_user = User(
        email=data['email'],
        username=data['username'],
        password=hashed_password,
        fname=data['fname'],
        lname=data['lname']
    )
    db.session.add(new_user)
    db.session.commit()
    token = jwt.encode({'user_id': new_user.id}, 'your-secret-key', algorithm='HS256')
    user_data = {
        "id": new_user.id,
        "message": "Registration successful",
        "email": new_user.email,
        "username": new_user.username,
        "fname": new_user.fname,
        "lname": new_user.lname
        # Add other fields as needed
    }
    return jsonify({"result": user_data, "token": token}), 201

# Multipart registration (for Android frontend)
@register_bp.route('/api/user/register', methods=['POST'])
def api_register_user_multipart():
    form = request.form
    email = form.get('email')
    password = form.get('password')
    fname = form.get('fname')
    lname = form.get('lname')
    username = form.get('username')
    phone = form.get('phone')
    about = form.get('about')
    manual_city = form.get('manual_city')
    users_types_id = form.get('users_types_id')

    # Required fields
    if not email or not password or not fname or not lname:
        return jsonify({'error': 'Missing required fields'}), 400

    if User.query.filter_by(email=email).first():
        return jsonify({'error': 'Email already registered'}), 409
    if username and User.query.filter_by(username=username).first():
        return jsonify({'error': 'Username already taken'}), 409

    hashed_password = generate_password_hash(password)
    new_user = User(
        email=email,
        username=username if username else email,
        password=hashed_password,
        fname=fname,
        lname=lname,
        phone=phone,
        about=about,
        manual_city=manual_city,
        users_types_id=users_types_id
    )
    db.session.add(new_user)
    db.session.commit()
    token = jwt.encode({'user_id': new_user.id}, 'your-secret-key', algorithm='HS256')
    # Build full user dict for frontend
    user_data = {
        "id": new_user.id,
        "message": "Registration successful",
        "email": new_user.email,
        "username": new_user.username,
        "fname": new_user.fname,
        "lname": new_user.lname,
        "phone": new_user.phone,
        "about": new_user.about,
        "manual_city": new_user.manual_city,
        "users_types_id": new_user.users_types_id,
        "profile_picture_url": getattr(new_user, 'profile_picture_url', None),
        "skills": [], # Add skills if you have them
        "broadcast": getattr(new_user, 'broadcast', None),
        "other_skill": getattr(new_user, 'other_skill', None),
        "confirmed": getattr(new_user, 'confirmed', True),
        "device_token": getattr(new_user, 'device_token', None),
        "twitter_id": getattr(new_user, 'twitter_id', None)
    }
    return jsonify({"result": user_data, "token": token}), 201
