package com.networkedcapital.rep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.networkedcapital.rep.domain.model.ActiveChat

/**
 * Room entity for caching ActiveChat data offline.
 * Maps to ActiveChat domain model for use in the app.
 */
@Entity(tableName = "active_chats")
data class ActiveChatEntity(
    @PrimaryKey val id: Int,
    val usersId: Int?,
    val chatsId: Int?,
    val name: String,
    val type: String, // "DM" or "GROUP"
    val unreadCount: Int,
    val lastMessage: String?,
    val timestamp: String?,
    val profilePictureUrl: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Converts ActiveChatEntity to ActiveChat domain model
     */
    fun toDomainModel(): ActiveChat {
        return ActiveChat(
            id = id,
            usersId = usersId,
            chatsId = chatsId,
            name = name,
            type = type,
            unreadCount = unreadCount,
            lastMessage = lastMessage,
            timestamp = timestamp,
            profilePictureUrl = profilePictureUrl
        )
    }

    companion object {
        /**
         * Converts ActiveChat domain model to ActiveChatEntity
         */
        fun fromDomainModel(chat: ActiveChat): ActiveChatEntity {
            return ActiveChatEntity(
                id = chat.id,
                usersId = chat.usersId,
                chatsId = chat.chatsId,
                name = chat.name,
                type = chat.type,
                unreadCount = chat.unreadCount,
                lastMessage = chat.lastMessage,
                timestamp = chat.timestamp,
                profilePictureUrl = chat.profilePictureUrl
            )
        }
    }
}
