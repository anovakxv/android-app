package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.data.api.UserApiService
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
}