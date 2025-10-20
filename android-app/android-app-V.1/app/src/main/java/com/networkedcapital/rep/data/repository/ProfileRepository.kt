package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.data.api.GoalApiService
import com.networkedcapital.rep.data.api.PortalApiService
import com.networkedcapital.rep.data.api.ProfileApiService
import com.networkedcapital.rep.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int): Flow<Result<User>>
    suspend fun getSkills(): Flow<Result<List<Skill>>>
    suspend fun getWrites(userId: Int): Flow<Result<List<WriteBlock>>>
    suspend fun addWrite(title: String, content: String): Flow<Result<WriteBlock>>
    suspend fun editWrite(writeId: Int, title: String, content: String, order: Int?): Flow<Result<WriteBlock>>
    suspend fun deleteWrite(writeId: Int): Flow<Result<Unit>>
    suspend fun getUserPortals(userId: Int): Flow<Result<List<Portal>>>
    suspend fun getUserGoals(userId: Int): Flow<Result<List<Goal>>>
    suspend fun isBlocked(userId: Int): Flow<Result<Boolean>>
    suspend fun blockUser(userId: Int): Flow<Result<Unit>>
    suspend fun unblockUser(userId: Int): Flow<Result<Unit>>
    suspend fun flagUser(userId: Int, reason: String): Flow<Result<Unit>>
    suspend fun addToNetwork(targetUserId: Int): Flow<Result<Unit>>
}

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val portalApiService: PortalApiService,
    private val goalApiService: GoalApiService
) : ProfileRepository {

    override suspend fun getUserProfile(userId: Int): Flow<Result<User>> = flow {
        try {
            val response = profileApiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.result))
            } else {
                emit(Result.failure(Exception("Failed to load profile: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getSkills(): Flow<Result<List<Skill>>> = flow {
        try {
            val response = profileApiService.getSkills()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.result))
            } else {
                emit(Result.failure(Exception("Failed to load skills: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getWrites(userId: Int): Flow<Result<List<WriteBlock>>> = flow {
        try {
            val response = profileApiService.getWrites(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.result))
            } else {
                emit(Result.failure(Exception("Failed to load writes: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addWrite(title: String, content: String): Flow<Result<WriteBlock>> = flow {
        try {
            val params = mapOf(
                "title" to title,
                "content" to content
            )
            val response = profileApiService.addWrite(params)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to add write: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun editWrite(writeId: Int, title: String, content: String, order: Int?): Flow<Result<WriteBlock>> = flow {
        try {
            val params = mutableMapOf<String, Any>(
                "title" to title,
                "content" to content
            )
            if (order != null) {
                params["order"] = order
            }
            val response = profileApiService.editWrite(writeId, params)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to edit write: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteWrite(writeId: Int): Flow<Result<Unit>> = flow {
        try {
            val response = profileApiService.deleteWrite(writeId)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to delete write: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getUserPortals(userId: Int): Flow<Result<List<Portal>>> = flow {
        try {
            val response = portalApiService.filterNetworkPortals(userId, "open")
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.result))
            } else {
                emit(Result.failure(Exception("Failed to load portals: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getUserGoals(userId: Int): Flow<Result<List<Goal>>> = flow {
        try {
            val response = goalApiService.getGoalsForUser(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.aGoals))
            } else {
                emit(Result.failure(Exception("Failed to load goals: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun isBlocked(userId: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = profileApiService.isBlocked(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!.is_blocked))
            } else {
                emit(Result.failure(Exception("Failed to check block status: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun blockUser(userId: Int): Flow<Result<Unit>> = flow {
        try {
            val params = mapOf("users_id" to userId)
            val response = profileApiService.blockUser(params)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to block user: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun unblockUser(userId: Int): Flow<Result<Unit>> = flow {
        try {
            val params = mapOf("users_id" to userId)
            val response = profileApiService.unblockUser(params)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to unblock user: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun flagUser(userId: Int, reason: String): Flow<Result<Unit>> = flow {
        try {
            val params = mapOf(
                "users_id" to userId,
                "reason" to reason
            )
            val response = profileApiService.flagUser(params)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to flag user: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addToNetwork(targetUserId: Int): Flow<Result<Unit>> = flow {
        try {
            val params = mapOf(
                "action" to "add",
                "target_user_id" to targetUserId
            )
            val response = profileApiService.networkAction(params)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to add to network: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
