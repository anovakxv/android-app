package com.networkedcapital.rep.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.domain.model.Goal
import com.networkedcapital.rep.domain.model.BarChartData
import com.networkedcapital.rep.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import com.networkedcapital.rep.data.remote.GoalsApiService
import com.networkedcapital.rep.domain.model.FeedItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoalsDetailViewModel : ViewModel() {
    // Replace with your backend base URL
    private val BASE_URL = "https://rep-june2025.onrender.com" // TODO: update to your actual backend
    private val api: GoalsApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
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
                val response = api.getGoalDetail(goalId)
                _goal.value = response.goal
                _feed.value = response.feed
                // Try to get team from details response, fallback to /users endpoint if empty
                if (response.team.isNullOrEmpty()) {
                    try {
                        val teamResponse = api.getGoalUsers(goalId)
                        _team.value = teamResponse.result
                    } catch (te: Exception) {
                        _team.value = emptyList()
                    }
                } else {
                    _team.value = response.team
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

@Serializable
data class GoalDetailResponse(
    val goal: Goal,
    val feed: List<FeedItem>,
    val team: List<User>
)

// Remove duplicate FeedItem declaration (already defined elsewhere)
