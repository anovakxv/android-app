# Rep
# Copyright (c) 2025 Networked Capital Inc. All rights reserved.
# Created by Adam Novak: June 2025

from app.models.Purpose_Models.Category import Category
from app.models.People_Models import User
from app.models.Purpose_Models.City import City
from app.models.Purpose_Models.PortalGraphicSection import PortalGraphicSection
from app.models.s3Content_Models import S3Content
from app.models.ValueMetric_Models import Goal, GoalMetric, GoalPreInvite, GoalProgressLog, GoalTeam, GoalType, ReportingIncrement

    # ValueMetric_Models
    from app.models.ValueMetric_Models import Goal, GoalMetric, GoalType, GoalTeam, GoalProgressLog, GoalPreInvite, ReportingIncrement

    # Activities
    from app.models import Activities

    db.init_app(app)
    socketio.init_app(app)

    from app.routes.api import api_bp
    app.register_blueprint(api_bp)

    return app

