package com.networkedcapital.rep.data.api

import com.google.gson.annotations.SerializedName
import com.networkedcapital.rep.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface GoalApiService {
    
    @GET(ApiConfig.GOALS_LIST)
    suspend fun getGoals(@Query("portal_id") portalId: String? = null): Response<List<Goal>>
    
    @GET(ApiConfig.GOAL_DETAILS)
    suspend fun getGoalDetails(
        @Query("goals_id") goalId: Int,
        @Query("num_periods") numPeriods: Int = 7
    ): Response<GoalDetailResponse>
    
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
    
    @GET(ApiConfig.GOALS_LIST)
    suspend fun getUserGoals(@Query("users_id") userId: Int): Response<PortalGoalsApiResponse>
    
    @POST("api/goals/join_leave")
    suspend fun joinOrLeaveGoal(@Body request: JoinLeaveGoalRequest): Response<JoinLeaveGoalResponse>
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

data class JoinLeaveGoalRequest(
    val aGoalsIDs: List<Int>,
    val todo: String  // "join" or "leave"
)

data class JoinLeaveGoalResponse(
    val result: Map<Int, String>,  // Map of goalId to result status
    val team_sizes: Map<Int, Int>? = null  // Map of goalId to team size
)

data class GoalDetailResponse(
    val result: GoalDetailData
)

data class GoalDetailData(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val progress: Double? = null,
    @SerializedName("progress_percent") val progressPercent: Double? = null,
    val quota: Double? = null,
    @SerializedName("filled_quota") val filledQuota: Double? = null,
    val metricName: String? = null,
    val typeName: String? = null,
    val reportingName: String? = null,
    val quotaString: String? = null,
    val valueString: String? = null,
    val chartData: List<BarChartData>? = null,
    val creatorId: Int? = null,
    val portalId: Int? = null,
    val portalName: String? = null,
    val team: List<User>? = null,
    @SerializedName("aLatestProgress") val aLatestProgress: List<ProgressLog>? = null
)

data class ProgressLog(
    val id: Int,
    @SerializedName("users_id") val usersId: Int? = null,
    @SerializedName("added_value") val addedValue: Double? = null,
    val note: String? = null,
    val value: Double? = null,
    val timestamp: String? = null,
    val aAttachments: List<ProgressAttachment>? = null
)

data class ProgressAttachment(
    val id: Int,
    @SerializedName("file_url") val fileUrl: String? = null,
    @SerializedName("is_image") val isImage: Boolean? = null
)
