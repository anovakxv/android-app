package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.data.api.UserApiService
import com.networkedcapital.rep.presentation.settings.NotificationSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun getNetworkUsersNotInChat(chatId: Int): List<User> {
        return try {
            val response = userApiService.getNetworkUsersNotInChat(chatId)
            response.result
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): Flow<Result<Unit>> = flow {
        try {
            val response = userApiService.updateNotificationSettings(
                mapOf(
                    "pushNotificationsEnabled" to settings.pushNotificationsEnabled,
                    "notifDirectMessages" to settings.notifDirectMessages,
                    "notifGroupMessages" to settings.notifGroupMessages,
                    "notifGoalInvites" to settings.notifGoalInvites
                )
            )
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to update notification settings: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getNetworkMembers(): Flow<Result<List<User>>> = flow {
        try {
            val response = userApiService.getNetworkMembers()
            emit(Result.success(response.result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun blockUser(userId: Int): Flow<Result<Unit>> = flow {
        try {
            val response = userApiService.blockUser(userId)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to block user: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun flagUser(userId: Int, reason: String): Flow<Result<Unit>> = flow {
        try {
            val response = userApiService.flagUser(mapOf("users_id" to userId, "reason" to reason))
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to flag user: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}