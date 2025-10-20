package com.networkedcapital.rep.presentation.invites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.InviteRepository
import com.networkedcapital.rep.domain.model.GoalTeamInvite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InviteState(
    val invites: List<GoalTeamInvite> = emptyList(),
    val isLoading: Boolean = false,
    val processingInviteId: Int? = null,
    val alertMessage: String? = null,
    val showAlert: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val inviteRepository: InviteRepository
) : ViewModel() {

    private val _inviteState = MutableStateFlow(InviteState())
    val inviteState: StateFlow<InviteState> = _inviteState.asStateFlow()

    private var currentUserId: Int = 0

    fun initialize(userId: Int) {
        this.currentUserId = userId
        loadPendingInvites()
    }

    fun loadPendingInvites() {
        viewModelScope.launch {
            _inviteState.value = _inviteState.value.copy(isLoading = true, errorMessage = null)

            // Mark invites as read
            inviteRepository.markInvitesRead()
                .catch { /* Ignore errors for marking read */ }
                .firstOrNull() // Just trigger the call, don't wait

            // Load pending invites
            inviteRepository.getPendingInvites()
                .catch { e ->
                    _inviteState.value = _inviteState.value.copy(
                        errorMessage = "Failed to load invites: ${e.message}",
                        isLoading = false
                    )
                }
                .firstOrNull()?.fold(
                        onSuccess = { invites ->
                            _inviteState.value = _inviteState.value.copy(
                                invites = invites,
                                isLoading = false
                            )
                        },
                        onFailure = { e ->
                            _inviteState.value = _inviteState.value.copy(
                                errorMessage = "Failed to load invites: ${e.message}",
                                isLoading = false
                            )
                        }
                    )
        }
    }

    fun acceptInvite(invite: GoalTeamInvite) {
        viewModelScope.launch {
            _inviteState.value = _inviteState.value.copy(processingInviteId = invite.id)

            inviteRepository.respondToInvite(
                goalId = invite.goals_id,
                action = "accept",
                userId = currentUserId
            )
                .catch { e ->
                    _inviteState.value = _inviteState.value.copy(
                        processingInviteId = null,
                        alertMessage = "Failed to accept invite: ${e.message}",
                        showAlert = true
                    )
                }
                .firstOrNull()?.fold(
                        onSuccess = {
                            // Remove invite from list
                            val updatedInvites = _inviteState.value.invites
                                .filter { it.id != invite.id }

                            _inviteState.value = _inviteState.value.copy(
                                invites = updatedInvites,
                                processingInviteId = null,
                                alertMessage = "You've joined the goal team!",
                                showAlert = true
                            )
                        },
                        onFailure = { e ->
                            _inviteState.value = _inviteState.value.copy(
                                processingInviteId = null,
                                alertMessage = "Failed to accept invite: ${e.message}",
                                showAlert = true
                            )
                        }
                    )
        }
    }

    fun declineInvite(invite: GoalTeamInvite) {
        viewModelScope.launch {
            _inviteState.value = _inviteState.value.copy(processingInviteId = invite.id)

            inviteRepository.respondToInvite(
                goalId = invite.goals_id,
                action = "decline",
                userId = currentUserId
            )
                .catch { e ->
                    _inviteState.value = _inviteState.value.copy(
                        processingInviteId = null,
                        alertMessage = "Failed to decline invite: ${e.message}",
                        showAlert = true
                    )
                }
                .firstOrNull()?.fold(
                        onSuccess = {
                            // Remove invite from list
                            val updatedInvites = _inviteState.value.invites
                                .filter { it.id != invite.id }

                            _inviteState.value = _inviteState.value.copy(
                                invites = updatedInvites,
                                processingInviteId = null,
                                alertMessage = "Invite declined",
                                showAlert = true
                            )
                        },
                        onFailure = { e ->
                            _inviteState.value = _inviteState.value.copy(
                                processingInviteId = null,
                                alertMessage = "Failed to decline invite: ${e.message}",
                                showAlert = true
                            )
                        }
                    )
        }
    }

    fun dismissAlert() {
        _inviteState.value = _inviteState.value.copy(
            showAlert = false,
            alertMessage = null
        )
    }

    fun clearError() {
        _inviteState.value = _inviteState.value.copy(errorMessage = null)
    }

    /**
     * Get count of unread pending invites for badge display
     */
    fun getUnreadInviteCount(): Int {
        return _inviteState.value.invites.size
    }
}
