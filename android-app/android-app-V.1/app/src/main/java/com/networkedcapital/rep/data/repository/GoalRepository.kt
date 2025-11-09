package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.data.api.*
import com.networkedcapital.rep.domain.model.ReportingIncrement
import com.networkedcapital.rep.domain.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalApiService: GoalApiService
) {

    suspend fun getReportingIncrements(): Flow<Result<List<ReportingIncrement>>> = flow {
        try {
            val response = goalApiService.getReportingIncrements()
            if (response.isSuccessful) {
                val incrementsResponse = response.body()
                if (incrementsResponse != null) {
                    emit(Result.success(incrementsResponse.reportingIncrements))
                } else {
                    emit(Result.failure(Exception("No reporting increments found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to load reporting increments: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createGoal(
        title: String,
        subtitle: String,
        description: String,
        goalType: String,
        quota: Int,
        reportingIncrementsId: Int,
        userId: Int,
        portalId: Int? = null,
        metric: String? = null
    ): Flow<Result<String>> = flow {
        try {
            val request = GoalCreateRequest(
                title = title,
                subtitle = subtitle,
                description = description,
                goal_type = goalType,
                quota = quota,
                reporting_increments_id = reportingIncrementsId,
                user_id = userId,
                portals_id = portalId,
                metric = metric
            )
            val response = goalApiService.createGoal(request)
            if (response.isSuccessful) {
                val goalResponse = response.body()
                if (goalResponse?.result != null) {
                    emit(Result.success(goalResponse.result))
                } else if (goalResponse?.error != null) {
                    emit(Result.failure(Exception(goalResponse.error)))
                } else {
                    emit(Result.success("Goal created successfully"))
                }
            } else {
                emit(Result.failure(Exception("Failed to create goal: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun editGoal(
        goalId: Int,
        title: String,
        subtitle: String,
        description: String,
        goalType: String,
        quota: Int,
        reportingIncrementsId: Int,
        userId: Int,
        portalId: Int? = null,
        metric: String? = null
    ): Flow<Result<String>> = flow {
        try {
            val request = GoalEditRequest(
                goals_id = goalId,
                title = title,
                subtitle = subtitle,
                description = description,
                goal_type = goalType,
                quota = quota,
                reporting_increments_id = reportingIncrementsId,
                user_id = userId,
                portals_id = portalId,
                metric = metric
            )
            val response = goalApiService.editGoal(request)
            if (response.isSuccessful) {
                val goalResponse = response.body()
                if (goalResponse?.result != null) {
                    emit(Result.success(goalResponse.result))
                } else if (goalResponse?.error != null) {
                    emit(Result.failure(Exception(goalResponse.error)))
                } else {
                    emit(Result.success("Goal updated successfully"))
                }
            } else {
                emit(Result.failure(Exception("Failed to update goal: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
