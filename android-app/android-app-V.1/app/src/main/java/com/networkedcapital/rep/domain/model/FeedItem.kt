package com.networkedcapital.rep.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedItem(
    val id: Int = 0,
    val userName: String = "",
    val date: String = "",
    val value: String = "",
    val note: String = ""
)
