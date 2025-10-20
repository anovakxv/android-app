package com.networkedcapital.rep.data.api

import com.networkedcapital.rep.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {
    // User Profile
    @GET("api/user/profile")
    suspend fun getUserProfile(
        @Query("users_id") userId: Int
    ): Response<UserProfileResponse>

    // Skills
    @GET("api/user/get_skills")
    suspend fun getSkills(): Response<SkillsResponse>

    // Writes/Blog Posts
    @GET("api/user/writes")
    suspend fun getWrites(
        @Query("users_id") userId: Int
    ): Response<WriteBlocksResponse>

    @POST("api/user/write")
    suspend fun addWrite(
        @Body write: Map<String, String>
    ): Response<WriteBlock>

    @PUT("api/user/write/{id}")
    suspend fun editWrite(
        @Path("id") writeId: Int,
        @Body write: Map<String, Any>
    ): Response<WriteBlock>

    @DELETE("api/user/write/{id}")
    suspend fun deleteWrite(
        @Path("id") writeId: Int
    ): Response<Unit>

    // Block/Unblock
    @GET("api/user/is_blocked")
    suspend fun isBlocked(
        @Query("users_id") userId: Int
    ): Response<BlockStatusResponse>

    @POST("api/user/block")
    suspend fun blockUser(
        @Body params: Map<String, Int>
    ): Response<Unit>

    @POST("api/user/unblock")
    suspend fun unblockUser(
        @Body params: Map<String, Int>
    ): Response<Unit>

    // Flag User
    @POST("api/user/flag_user")
    suspend fun flagUser(
        @Body params: Map<String, Any>
    ): Response<Unit>

    // Network Action
    @POST("api/user/network_action")
    suspend fun networkAction(
        @Body params: Map<String, Any>
    ): Response<Unit>
}

data class UserProfileResponse(
    val result: User
)

data class SkillsResponse(
    val result: List<Skill>
)

data class BlockStatusResponse(
    val is_blocked: Boolean
)
