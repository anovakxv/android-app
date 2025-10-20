package com.networkedcapital.rep.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.BarChartData
import com.networkedcapital.rep.domain.model.User
import com.networkedcapital.rep.domain.model.FeedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import com.networkedcapital.rep.data.remote.GoalsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoalsDetailViewModel : ViewModel() {

    // S3 base URL for profile images
    private val s3BaseUrl = "https://rep-app-dbbucket.s3.us-west-2.amazonaws.com/"

    // Helper to patch/construct full profile image URL
    fun patchProfilePictureUrl(imageNameOrUrl: String?): String? {
        if (imageNameOrUrl.isNullOrBlank()) return null
        return if (imageNameOrUrl.startsWith("http")) imageNameOrUrl else s3BaseUrl + imageNameOrUrl
    }

    // Replace with your backend base URL
    private val BASE_URL = "https://rep-june2025.onrender.com" // Must match Swift/iOS

    // TODO: JWT token retrieval needs proper Hilt integration with Application context
    // For now, returning null - this ViewModel needs to be refactored to use Hilt @Inject
    private fun getJwtToken(): String? {
        // TODO: Inject SharedPreferences or TokenManager via Hilt
        return null
    }

    // OkHttp interceptor to inject JWT token
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        getJwtToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val api: GoalsApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(GoalsApiService::class.java)

    private val _goal = MutableStateFlow<Goal?>(null)
    val goal: StateFlow<Goal?> = _goal

    private val _feed = MutableStateFlow<List<FeedItem>>(emptyList())
    val feed: StateFlow<List<FeedItem>> = _feed

    private val _team = MutableStateFlow<List<User>>(emptyList())
    val team: StateFlow<List<User>> = _team

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGoal(goalId: Int) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Use same endpoint and params as Swift/iOS
                val response = api.getGoalDetail(goalId, numPeriods = 7)
                _goal.value = response.goal

                // Patch FeedItem with profile image URLs if possible
                val patchedFeed = response.feed.map { feedItem ->
                    val user = response.team.find { it.id == feedItem.userId }
                    val imageUrl = patchProfilePictureUrl(user?.imageName ?: user?.profile_picture_url)
                    feedItem.copy(profilePictureUrl = imageUrl)
                }
                _feed.value = patchedFeed

                // Patch Team users with profile image URLs
                val patchedTeam = response.team.map { user ->
                    val imageUrl = patchProfilePictureUrl(user.imageName ?: user.profile_picture_url)
                    user.copy(profile_picture_url = imageUrl)
                }
                _team.value = patchedTeam

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Process payment
    fun processPayment(goalId: Int, portalId: Int, amount: Double, message: String) {
        viewModelScope.launch {
            // TODO: Implement payment processing API call
            // Example:
            // val result = api.processPayment(goalId, portalId, amount, message)
            // Handle result and update state as needed
        }
    }

    // Join team
    fun joinRecruitingGoal(goalId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            // TODO: Implement team joining API call
            // Example:
            // val success = api.joinTeam(goalId)
            // onComplete(success)
            onComplete(true) // or false on failure
        }
    }

    // Create team chat
    fun createTeamChat(goalId: Int, title: String, onComplete: (Int?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // TODO: Implement chat creation API call
                // val chatId = api.createTeamChat(goalId, title)
                val chatId = 123 // Replace with actual API response
                onComplete(chatId, null)
            } catch (e: Exception) {
                onComplete(null, e.message)
            }
        }
    }

    // Helper to get current user ID (stub, replace with real logic)
    fun getCurrentUserId(): Int {
        // TODO: Implement user ID retrieval
        return 0
    }
}

@Serializable
data class GoalDetailResponse(
    val goal: Goal,
    val feed: List<FeedItem>,
    val team: List<User>
)