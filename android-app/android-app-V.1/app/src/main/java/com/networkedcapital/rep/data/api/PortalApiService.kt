package com.networkedcapital.rep.data.api

import com.networkedcapital.rep.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface PortalApiService {
    
    @GET(ApiConfig.PORTALS_LIST)
    suspend fun getPortals(): Response<List<Portal>>
    
    @POST(ApiConfig.PORTALS_FILTER)
    suspend fun filterPortals(@Body request: PortalFilterRequest): Response<List<Portal>>
    
    @GET(ApiConfig.PORTAL_DETAILS)
    suspend fun getPortalDetails(@Query("portal_id") portalId: String): Response<Portal>
    
    @POST(ApiConfig.PORTAL_CREATE)
    suspend fun createPortal(@Body portal: CreatePortalRequest): Response<Portal>
    
    @PUT(ApiConfig.PORTAL_EDIT)
    suspend fun updatePortal(@Body portal: UpdatePortalRequest): Response<Portal>
    
    @DELETE(ApiConfig.PORTAL_DELETE)
    suspend fun deletePortal(@Query("portal_id") portalId: String): Response<Unit>
    
    @Multipart
    @POST("api/portal/upload_image")
    suspend fun uploadPortalImage(
        @Part("image") image: okhttp3.MultipartBody.Part,
        @Part("portal_id") portalId: okhttp3.RequestBody
    ): Response<ImageUploadResponse>
    
    @GET("api/portal/search_people")
    suspend fun searchPeople(@Query("search_term") searchTerm: String): Response<List<User>>
    
    @POST("api/portal/join")
    suspend fun joinPortal(@Body request: JoinPortalRequest): Response<Unit>
    
    @POST("api/portal/leave")
    suspend fun leavePortal(@Body request: LeavePortalRequest): Response<Unit>
}

data class PortalFilterRequest(
    val searchTerm: String? = null,
    val category: String? = null,
    val location: String? = null
)

data class CreatePortalRequest(
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val isPrivate: Boolean,
    val storyBlocks: List<StoryBlock>? = null
)

data class UpdatePortalRequest(
    val portalId: String,
    val name: String,
    val description: String,
    val category: String,
    val location: String,
    val isPrivate: Boolean,
    val storyBlocks: List<StoryBlock>? = null
)

data class JoinPortalRequest(
    val portalId: String
)

data class LeavePortalRequest(
    val portalId: String
)
