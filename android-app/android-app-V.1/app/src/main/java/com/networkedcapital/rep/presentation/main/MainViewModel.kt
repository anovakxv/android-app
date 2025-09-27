package com.networkedcapital.rep.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.AuthRepository
import com.networkedcapital.rep.data.repository.PortalRepository
import com.networkedcapital.rep.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: MainViewModel.MainPage = MainViewModel.MainPage.PORTALS,
    val selectedSection: Int = 2, // 0=OPEN, 1=NTWK, 2=ALL
    val showSearch: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val showOnlySafePortals: Boolean = false,
    val currentUser: User? = null,
    val portals: List<Portal> = emptyList(),
    val users: List<User> = emptyList(),
    val activeChats: List<ActiveChat> = emptyList(),
    val searchPortals: List<Portal> = emptyList(),
    val searchUsers: List<User> = emptyList()
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val portalRepository: PortalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun onSectionSelected(section: String) {
        val sectionIndex = when (section) {
            "OPEN" -> 0
            "NTWK" -> 1
            "ALL" -> 2
            else -> 2
        }
        _uiState.update { state -> state.copy(selectedSection = sectionIndex) }
        val userId = _uiState.value.currentUser?.id ?: 0
        viewModelScope.launch {
            when (_uiState.value.currentPage) {
                MainPage.PORTALS -> fetchPortals(userId, sectionIndex, _uiState.value.showOnlySafePortals)
                MainPage.PEOPLE -> fetchPeople(userId, sectionIndex)
            }
        }
    }

    enum class MainPage { PORTALS, PEOPLE }

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Debounced search
        searchQuery
            .debounce(400)
            .onEach { query ->
                if (_uiState.value.showSearch && query.isNotEmpty()) {
                    performSearch(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadData(userId: Int) {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch current user
                fetchCurrentUser()
                
                // Fetch content based on current page and section
                when (_uiState.value.currentPage) {
                    MainPage.PORTALS -> fetchPortals(userId, _uiState.value.selectedSection)
                    MainPage.PEOPLE -> fetchPeople(userId, _uiState.value.selectedSection)
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun onSectionChanged(section: Int, userId: Int) {
        _uiState.update { state -> state.copy(selectedSection = section) }
        viewModelScope.launch {
            when (_uiState.value.currentPage) {
                MainPage.PORTALS -> fetchPortals(userId, section)
                MainPage.PEOPLE -> fetchPeople(userId, section)
            }
        }
    }

    fun togglePage(userId: Int) {
        val newPage = when (_uiState.value.currentPage) {
            MainPage.PORTALS -> MainPage.PEOPLE
            MainPage.PEOPLE -> MainPage.PORTALS
        }
        _uiState.update { state ->
            state.copy(
                currentPage = newPage,
                searchQuery = "",
                searchPortals = emptyList(),
                searchUsers = emptyList()
            )
        }
        _searchQuery.value = ""
        viewModelScope.launch {
            when (newPage) {
                MainPage.PORTALS -> fetchPortals(userId, _uiState.value.selectedSection)
                MainPage.PEOPLE -> fetchPeople(userId, _uiState.value.selectedSection)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    fun toggleSearch() {
        val showSearch = !_uiState.value.showSearch
        _uiState.update { state ->
            state.copy(
                showSearch = showSearch,
                searchQuery = if (!showSearch) "" else state.searchQuery,
                searchPortals = if (!showSearch) emptyList() else state.searchPortals,
                searchUsers = if (!showSearch) emptyList() else state.searchUsers
            )
        }
        if (!showSearch) {
            _searchQuery.value = ""
        }
    }

    fun toggleSafePortals(userId: Int) {
        val newValue = !_uiState.value.showOnlySafePortals
        _uiState.update { state -> state.copy(showOnlySafePortals = newValue) }
        if (_uiState.value.currentPage == MainPage.PORTALS) {
            viewModelScope.launch {
                fetchPortals(userId, _uiState.value.selectedSection, newValue)
            }
        }
    }

    private suspend fun fetchPortals(userId: Int, section: Int, safeOnly: Boolean = false) {
        _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
        try {
            val tab = when (section) {
                0 -> "open"
                1 -> "ntwk"
                2 -> "all"
                else -> "open"
            }
            val portals = portalRepository.getFilteredPortals(userId, tab, safeOnly)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    portals = portals,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load portals"
                )
            }
        }
    }

    private suspend fun fetchPeople(userId: Int, section: Int) {
        _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
        try {
            if (section == 0) {
                // Fetch active chats
                val chats = portalRepository.getActiveChats(userId)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        activeChats = chats,
                        users = emptyList(),
                        errorMessage = null
                    )
                }
            } else {
                // Fetch users
                val tab = if (section == 1) "ntwk" else "all"
                val users = portalRepository.getFilteredPeople(userId, tab)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        users = users,
                        activeChats = emptyList(),
                        errorMessage = null
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load people"
                )
            }
        }
    }

    private suspend fun fetchCurrentUser() {
        try {
            authRepository.getCurrentUser()
                .collect { result ->
                    result.getOrNull()?.let { user ->
                        _uiState.update { state -> state.copy(currentUser = user) }
                    }
                }
        } catch (e: Exception) {
            // Ignore error, user will remain null
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { state -> state.copy(isSearching = true) }
            try {
                when (_uiState.value.currentPage) {
                    MainPage.PORTALS -> {
                        val portals = if (_uiState.value.selectedSection == 2) {
                            portalRepository.searchPortals(query)
                        } else {
                            _uiState.value.portals.filter {
                                it.name.contains(query, ignoreCase = true)
                            }
                        }
                        _uiState.update { state ->
                            state.copy(
                                isSearching = false,
                                searchPortals = portals,
                                searchUsers = emptyList()
                            )
                        }
                    }
                    MainPage.PEOPLE -> {
                        val users: List<User> = if (_uiState.value.selectedSection == 2) {
                            // Ensure the result is cast to List<User>
                            @Suppress("UNCHECKED_CAST")
                            portalRepository.searchPeople(query) as? List<User> ?: emptyList()
                        } else {
                            _uiState.value.users.filter {
                                it.displayName.contains(query, ignoreCase = true)
                            }
                        }
                        _uiState.update { state ->
                            state.copy(
                                isSearching = false,
                                searchUsers = users,
                                searchPortals = emptyList()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isSearching = false,
                        errorMessage = e.message ?: "Search failed"
                    )
                }
            }
        }
    }
}
