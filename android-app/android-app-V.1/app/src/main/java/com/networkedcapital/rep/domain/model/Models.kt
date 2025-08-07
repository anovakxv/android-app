package com.networkedcapital.rep.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val email: String? = null,
    val about: String? = null,
    val broadcast: String? = null,
    val phone: String? = null,
    val cities_id: Int? = null,
    val users_types_id: Int? = null,
    val fname: String? = null,
    val lname: String? = null,
    val username: String? = null,
    val confirmed: Boolean = true,
    val device_token: String? = null,
    val twitter_id: String? = null,
    val manual_city: String? = null,
    val other_skill: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val last_login: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val profile_picture_url: String? = null,
    val skills: List<Skill>? = null,
    val userType: UserType? = null,
    // Additional fields from iOS app
    val fullName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val imageName: String? = null,
    val userType_string: String? = null,
    val city: String? = null,
    val lastMessage: String? = null,
    val lastMessageDate: String? = null
) : Parcelable {
    val displayName: String
        get() = fullName ?: fname?.let { fn -> 
            lname?.let { ln -> "$fn $ln" } ?: fn
        } ?: firstName?.let { fn ->
            lastName?.let { ln -> "$fn $ln" } ?: fn
        } ?: username ?: email ?: "Unknown User"
    
    val repTypeAndCity: String
        get() {
            val type = userType?.name ?: userType_string ?: ""
            val cityStr = city ?: manual_city ?: ""
            return when {
                type.isNotEmpty() && cityStr.isNotEmpty() -> "Rep Type: $type   City: $cityStr"
                type.isNotEmpty() -> "Rep Type: $type"
                cityStr.isNotEmpty() -> "City: $cityStr"
                else -> ""
            }
        }
}

@Parcelize
data class UserType(
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class Skill(
    val id: Int,
    val title: String
) : Parcelable

@Parcelize
data class Portal(
    val id: Int,
    val name: String,
    val subtitle: String? = null,
    val about: String? = null,
    val categoriesId: Int? = null,
    val citiesId: Int? = null,
    val leadId: Int? = null,
    val usersId: Int? = null,
    val usersCount: Int? = null,
    val mainImageUrl: String? = null
) : Parcelable

@Parcelize
data class Goal(
    val id: Int,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val progress: Double = 0.0,
    val progressPercent: Double = 0.0,
    val quota: Double = 0.0,
    val filledQuota: Double = 0.0,
    val metricName: String = "",
    val typeName: String = "",
    val reportingName: String = "",
    val quotaString: String = "",
    val valueString: String = "",
    val chartData: List<BarChartData> = emptyList(),
    val creatorId: Int = 0,
    val portalId: Int? = null
) : Parcelable

@Parcelize
data class BarChartData(
    val id: Int,
    val value: Double,
    val valueLabel: String,
    val bottomLabel: String
) : Parcelable

@Parcelize
data class Message(
    val id: Int,
    val senderId: Int,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val read: String? = null
) : Parcelable

enum class RepType(val displayName: String, val dbId: Int) {
    LEAD("Lead", 1),
    SPECIALIST("Specialist", 2),
    PARTNER("Partner", 3),
    FOUNDER("Founder", 4);
    
    companion object {
        fun fromDisplayName(name: String): RepType? = values().find { it.displayName == name }
        fun fromDbId(id: Int): RepType? = values().find { it.dbId == id }
    }
}

@Parcelize
data class StoryBlock(
    val id: Int? = null,
    val type: String, // "text", "image", "video"
    val content: String, // text content or URL for media
    val title: String? = null,
    val description: String? = null,
    val order: Int = 0
) : Parcelable

@Parcelize
data class ProgressUpdate(
    val id: Int,
    val goalId: Int,
    val userId: Int,
    val progress: Int,
    val note: String? = null,
    val timestamp: String
) : Parcelable

@Parcelize
data class Team(
    val id: Int,
    val name: String,
    val members: List<User> = emptyList(),
    val goals: List<Goal> = emptyList()
) : Parcelable
