package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.domain.model.User

interface UserRepository {
    suspend fun getNetworkUsersNotInChat(chatId: Int): List<User>
}