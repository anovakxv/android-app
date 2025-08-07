package com.networkedcapital.rep.data.api

import com.networkedcapital.rep.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface GoalApiService {
    
    @GET(ApiConfig.GOALS_LIST)
    suspend fun getGoals(@Query("portal_id") portalId: String? = null): Response<List<Goal>>
    
    @GET(ApiConfig.GOAL_DETAILS)
    suspend fun getGoalDetails(@Query("goal_id") goalId: String): Response<Goal>
    
    @POST(ApiConfig.GOAL_CREATE)
    suspend fun createGoal(@Body goal: CreateGoalRequest): Response<Goal>
    
    @PUT(ApiConfig.GOAL_EDIT)
    suspend fun updateGoal(@Body goal: UpdateGoalRequest): Response<Goal>
    
    @DELETE(ApiConfig.GOAL_DELETE)
    suspend fun deleteGoal(@Query("goal_id") goalId: String): Response<Unit>
    
    @POST(ApiConfig.GOAL_PROGRESS_UPDATE)
    suspend fun updateProgress(@Body request: UpdateProgressRequest): Response<Goal>
    
    @POST(ApiConfig.GOAL_TEAM_MANAGE)
    suspend fun manageTeam(@Body request: ManageTeamRequest): Response<Goal>
    
    @GET("api/goals/user_goals")
    suspend fun getUserGoals(): Response<List<Goal>>
    
    @POST("api/goals/join")
    suspend fun joinGoal(@Body request: JoinGoalRequest): Response<Goal>
    
    @POST("api/goals/leave")
    suspend fun leaveGoal(@Body request: LeaveGoalRequest): Response<Goal>
}

data class CreateGoalRequest(
    val title: String,
    val description: String,
    val targetDate: String, // ISO date string
    val portalId: String,
    val isPrivate: Boolean = false,
    val progressType: String = "percentage" // "percentage" or "checkpoints"
)

data class UpdateGoalRequest(
    val goalId: String,
    val title: String,
    val description: String,
    val targetDate: String,
    val isPrivate: Boolean = false,
    val progressType: String = "percentage"
)

data class UpdateProgressRequest(
    val goalId: String,
    val progress: Int, // 0-100 for percentage, checkpoint index for checkpoints
    val note: String? = null
)

data class ManageTeamRequest(
    val goalId: String,
    val action: String, // "add" or "remove"
    val userId: String
)

data class JoinGoalRequest(
    val goalId: String
)

data class LeaveGoalRequest(
    val goalId: String
)
