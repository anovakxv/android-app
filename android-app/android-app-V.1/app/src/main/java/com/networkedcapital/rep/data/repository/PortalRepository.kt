package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.data.api.*
import com.networkedcapital.rep.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepository @Inject constructor(
    private val portalApiService: PortalApiService
) {

    suspend fun getPortals(): Flow<Result<List<Portal>>> = flow {
        try {
            val response = portalApiService.getPortals()
            if (response.isSuccessful) {
                val portals = response.body() ?: emptyList()
                emit(Result.success(portals))
            } else {
                emit(Result.failure(Exception("Failed to get portals: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getFilteredPortals(userId: Int, tab: String, safeOnly: Boolean = false): List<Portal> {
        val limitParam = if (tab == "all") 50 else null
        val response = portalApiService.getFilteredPortals(userId, tab, limitParam, safeOnly)
        
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            throw Exception("Failed to get filtered portals: ${response.message()}")
        }
    }

    suspend fun getFilteredPeople(userId: Int, tab: String): List<User> {
        val limitParam = if (tab == "all") 50 else null
        val response = portalApiService.getFilteredPeople(userId, tab, limitParam)
        
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            throw Exception("Failed to get filtered people: ${response.message()}")
        }
    }

    suspend fun getActiveChats(userId: Int): List<ActiveChat> {
        val response = portalApiService.getActiveChats(userId)
        
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            throw Exception("Failed to get active chats: ${response.message()}")
        }
    }

    suspend fun searchPortals(query: String, limit: Int = 50): List<Portal> {
        val response = portalApiService.searchPortals(query, limit)
        
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            throw Exception("Failed to search portals: ${response.message()}")
        }
    }

    suspend fun searchPeople(query: String, limit: Int = 50): List<User> {
        val response = portalApiService.searchPeople(query, limit)
        
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            throw Exception("Failed to search people: ${response.message()}")
        }
    }

    suspend fun filterPortals(
        searchTerm: String? = null,
        category: String? = null,
        location: String? = null
    ): Flow<Result<List<Portal>>> = flow {
        try {
            val request = PortalFilterRequest(searchTerm, category, location)
            val response = portalApiService.filterPortals(request)
            if (response.isSuccessful) {
                val portals = response.body() ?: emptyList()
                emit(Result.success(portals))
            } else {
                emit(Result.failure(Exception("Failed to filter portals: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getPortalDetails(portalId: String): Flow<Result<Portal>> = flow {
        try {
            val response = portalApiService.getPortalDetails(portalId)
            if (response.isSuccessful) {
                val portal = response.body()
                if (portal != null) {
                    emit(Result.success(portal))
                } else {
                    emit(Result.failure(Exception("Portal not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get portal details: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createPortal(
        name: String,
        description: String,
        category: String,
        location: String,
        isPrivate: Boolean,
        storyBlocks: List<StoryBlock>? = null
    ): Flow<Result<Portal>> = flow {
        try {
            val request = CreatePortalRequest(name, description, category, location, isPrivate, storyBlocks)
            val response = portalApiService.createPortal(request)
            if (response.isSuccessful) {
                val portal = response.body()
                if (portal != null) {
                    emit(Result.success(portal))
                } else {
                    emit(Result.failure(Exception("Failed to create portal")))
                }
            } else {
                emit(Result.failure(Exception("Portal creation failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun updatePortal(
        portalId: String,
        name: String,
        description: String,
        category: String,
        location: String,
        isPrivate: Boolean,
        storyBlocks: List<StoryBlock>? = null
    ): Flow<Result<Portal>> = flow {
        try {
            val request = UpdatePortalRequest(portalId, name, description, category, location, isPrivate, storyBlocks)
            val response = portalApiService.updatePortal(request)
            if (response.isSuccessful) {
                val portal = response.body()
                if (portal != null) {
                    emit(Result.success(portal))
                } else {
                    emit(Result.failure(Exception("Failed to update portal")))
                }
            } else {
                emit(Result.failure(Exception("Portal update failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun deletePortal(portalId: String): Flow<Result<Unit>> = flow {
        try {
            val response = portalApiService.deletePortal(portalId)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Portal deletion failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun uploadPortalImage(image: MultipartBody.Part, portalId: RequestBody): Flow<Result<String>> = flow {
        try {
            val response = portalApiService.uploadPortalImage(image, portalId)
            if (response.isSuccessful) {
                val uploadResponse = response.body()
                if (uploadResponse != null) {
                    emit(Result.success(uploadResponse.imageUrl))
                } else {
                    emit(Result.failure(Exception("Upload failed")))
                }
            } else {
                emit(Result.failure(Exception("Image upload failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun searchPeople(searchTerm: String): Flow<Result<List<User>>> = flow {
        try {
            val response = portalApiService.searchPeople(searchTerm)
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                emit(Result.success(users))
            } else {
                emit(Result.failure(Exception("People search failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun joinPortal(portalId: String): Flow<Result<Unit>> = flow {
        try {
            val request = JoinPortalRequest(portalId)
            val response = portalApiService.joinPortal(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to join portal: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun leavePortal(portalId: String): Flow<Result<Unit>> = flow {
        try {
            val request = LeavePortalRequest(portalId)
            val response = portalApiService.leavePortal(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to leave portal: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getPortalDetail(portalId: Int, userId: Int): Flow<Result<PortalDetail>> = flow {
        try {
            val response = portalApiService.getPortalDetail(portalId, userId)
            if (response.isSuccessful) {
                val portalDetailResponse = response.body()
                if (portalDetailResponse != null) {
                    emit(Result.success(portalDetailResponse.result))
                } else {
                    emit(Result.failure(Exception("Portal detail not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get portal detail: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getPortalGoals(portalId: Int): Flow<Result<List<Goal>>> = flow {
        try {
            val response = portalApiService.getPortalGoals(portalId)
            if (response.isSuccessful) {
                val goalsResponse = response.body()
                if (goalsResponse != null) {
                    emit(Result.success(goalsResponse.aGoals))
                } else {
                    emit(Result.success(emptyList()))
                }
            } else {
                emit(Result.failure(Exception("Failed to get portal goals: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getReportingIncrements(): Flow<Result<List<ReportingIncrement>>> = flow {
        try {
            val response = portalApiService.getReportingIncrements()
            if (response.isSuccessful) {
                val increments = response.body() ?: emptyList()
                emit(Result.success(increments))
            } else {
                emit(Result.failure(Exception("Failed to get reporting increments: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun flagPortal(portalId: Int, reason: String = ""): Flow<Result<Unit>> = flow {
        try {
            val request = FlagPortalRequest(portalId, reason)
            val response = portalApiService.flagPortal(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Failed to flag portal: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
