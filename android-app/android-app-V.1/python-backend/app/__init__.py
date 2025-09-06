from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_socketio import SocketIO
from config import Config

# Initialize extensions

# These will be imported by other modules
# so keep them at the top level

db = SQLAlchemy()
socketio = SocketIO(cors_allowed_origins="*")

# --- Force SQLAlchemy to register ---
from app.models.Purpose_Models.Category import Category
from app.models.People_Models.user import User
from app.models.Purpose_Models.City import City
from app.models.Purpose_Models.PortalGraphicSection import PortalGraphicSection
from app.models.s3Content_Models.s3Content import S3Content
from app.models.ValueMetric_Models import Goal, GoalMetric, GoalPreInvite, GoalProgressLog, GoalTeam, GoalType, ReportingIncrement

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # --- Import all models so Flask-Migrate can detect them ---
    from app.models.Purpose_Models.Category import Category
   
    # People_Models
    from app.models.People_Models.user import User
    from app.models.People_Models import UserType, UserSkill, UserNetwork, UserFollower, PasswordUpdater, Skill

    # Write_Models
    from app.models.People_Models.Write_Models import Writings_Model

    # Messaging_Models
    from app.models.People_Models.Messaging_Models import Direct_Messages, Group_Messages, GroupChatMetaData, GroupChatUsers

    # Purpose_Models
    from app.models.Purpose_Models import Portal, PortalUser, PortalEvent, PortalGraphicSection, PortalInvite, PortalTexts

    # s3Content_Models
    from app.models.s3Content_Models.s3Content import S3Content

    db.init_app(app)
    socketio.init_app(app)

    # RegisterUser endpoint
    from app.routes.User_Routes.RegisterUser import register_bp
    app.register_blueprint(register_bp)

    return app
