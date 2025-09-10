package com.networkedcapital.rep.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.AuthRepository
import com.networkedcapital.rep.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isRegistered: Boolean = false,
    val onboardingComplete: Boolean = false,
    val jwtToken: String = "",
    val userId: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun acceptTermsOfUse() {
        // This could call a backend endpoint if needed, or just update state
        _authState.value = _authState.value.copy(onboardingComplete = false)
    }

    fun continueAboutRep() {
        // This could call a backend endpoint if needed, or just update state
        _authState.value = _authState.value.copy(onboardingComplete = false)
    }

    fun saveProfile(
        name: String,
        email: String
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            authRepository.updateProfile(name, email)
                .catch { throwable ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Profile update failed"
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { user ->
                            _currentUser.value = user
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                onboardingComplete = true,
                                userId = user.id,
                                errorMessage = null
                            )
                        },
                        onFailure = { throwable ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Profile update failed"
                            )
                        }
                    )
                }
        }
    }
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

        fun login(email: String, password: String) {
            viewModelScope.launch {
                _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
                authRepository.login(email, password)
                    .catch { throwable ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Login failed"
                        )
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = { user ->
                                _currentUser.value = user
                                _authState.value = _authState.value.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    isRegistered = true,
                                    onboardingComplete = true,
                                    userId = user.id,
                                    errorMessage = null
                                )
                            },
                            onFailure = { throwable ->
                                _authState.value = _authState.value.copy(
                                    isLoading = false,
                                    errorMessage = throwable.message ?: "Login failed"
                                )
                            }
                        )
                    }
            }
        }
    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val isLoggedIn = authRepository.isLoggedIn()
        _authState.value = _authState.value.copy(
            isLoggedIn = isLoggedIn,
            isRegistered = isLoggedIn,
            onboardingComplete = isLoggedIn
        )
        if (isLoggedIn) {
            getCurrentUser()
        }
    }
    
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String = "",
        repType: String = "Lead",
        userTypeId: Int = 1 // Lead = 1, Specialist = 2, Partner = 3, Founder = 4
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.register(email, password, firstName, lastName, userTypeId, phone, null, null)
                .catch { throwable ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Registration failed"
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { user ->
                            _currentUser.value = user
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                isRegistered = true,
                                onboardingComplete = false, // Still needs onboarding
                                userId = user.id,
                                errorMessage = null
                            )
                        },
                        onFailure = { throwable ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                errorMessage = throwable.message ?: "Registration failed"
                            )
                        }
                    )
                }
        }
    }
    
    fun completeOnboarding() {
        _authState.value = _authState.value.copy(
            onboardingComplete = true
        )
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
                .catch { /* Handle error if needed */ }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _currentUser.value = null
                            _authState.value = AuthState()
                        },
                        onFailure = {
                            // Even if logout fails, clear local state
                            _currentUser.value = null
                            _authState.value = AuthState()
                        }
                    )
                }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authRepository.getProfile()
                .catch { /* Handle error silently for auto-fetch */ }
                .collect { result ->
                    result.fold(
                        onSuccess = { user ->
                            _currentUser.value = user
                        },
                        onFailure = {
                            // If profile fetch fails, user might need to re-login
                            _authState.value = _authState.value.copy(isLoggedIn = false)
                        }
                    )
                }
        }
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
