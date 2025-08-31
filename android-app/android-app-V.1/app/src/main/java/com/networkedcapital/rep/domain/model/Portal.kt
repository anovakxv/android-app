// In your Portal.kt or a similar model file

package com.networkedcapital.rep.domain.model

data class Portal(
    val id: Int,
    val name: String,
    val description: String,
    val company: String,
    val url: String?,
    val imageUrl: String?,
    val isSafe: Boolean,
    val leads: List<User>? = emptyList()
    // ... other properties as needed
)
