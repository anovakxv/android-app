package com.networkedcapital.rep.data.api

import com.networkedcapital.rep.domain.model.NTWKUsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {
    @GET("api/user/members_of_my_network")
    suspend fun getNetworkUsersNotInChat(
        @Query("not_in_chats_id") chatId: Int
    ): NTWKUsersResponse
}
