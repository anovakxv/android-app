package com.networkedcapital.rep.data.api

import com.networkedcapital.rep.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface MessagingApiService {
    
    @GET(ApiConfig.MESSAGES_CONVERSATIONS)
    suspend fun getConversations(): Response<List<Conversation>>
    
    @GET(ApiConfig.MESSAGES_LIST)
    suspend fun getMessages(@Query("user_id") userId: String): Response<List<Message>>
    
    @POST(ApiConfig.MESSAGES_SEND)
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Message>
    
    @DELETE(ApiConfig.MESSAGES_DELETE)
    suspend fun deleteMessage(@Query("message_id") messageId: String): Response<Unit>
    
    @PUT("api/messaging/mark_read")
    suspend fun markAsRead(@Body request: MarkReadRequest): Response<Unit>
    
    @GET("api/messaging/unread_count")
    suspend fun getUnreadCount(): Response<UnreadCountResponse>

    @GET("messages")
    suspend fun getMessages(
        @Query("chatId") chatId: Int
    ): Response<List<Message>>
}

data class Conversation(
    val userId: String,
    val user: User,
    val lastMessage: Message?,
    val unreadCount: Int
)

data class SendMessageRequest(
    val recipientId: String,
    val content: String,
    val messageType: String = "text" // "text", "image", etc.
)

data class MarkReadRequest(
    val userId: String // The user whose messages to mark as read
)

data class UnreadCountResponse(
    val totalUnreadCount: Int,
    val conversationCounts: Map<String, Int> // userId to unread count
)
