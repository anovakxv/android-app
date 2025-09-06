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
def api_login_user():
    # ...existing code...

@user_bp.route('/logout', methods=['POST'])
def api_logout_user():
    # ...existing code...

@user_bp.route('/forgot_password', methods=['POST'])
def api_forgot_password():
    # ...existing code...
