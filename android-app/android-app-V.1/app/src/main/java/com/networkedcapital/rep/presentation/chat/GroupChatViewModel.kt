package com.networkedcapital.rep.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.presentation.chat.GroupMessage
import com.networkedcapital.rep.presentation.chat.GroupMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GroupChatUiState(
    val groupName: String = "",
    val groupMembers: List<GroupMember> = emptyList(),
    val messages: List<GroupMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class GroupChatViewModel(
    private val chatId: Int,
    private val currentUserId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupChatUiState())
    val uiState: StateFlow<GroupChatUiState> = _uiState

    fun loadChat() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                // TODO: Replace with real API call
                val groupName = "Group Name"
                val members = listOf<GroupMember>() // Fetch members
                val messages = listOf<GroupMessage>() // Fetch messages
                _uiState.value = _uiState.value.copy(
                    groupName = groupName,
                    groupMembers = members,
                    messages = messages,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val trimmed = _uiState.value.inputText.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            // TODO: Send message to API and update messages list
            _uiState.value = _uiState.value.copy(inputText = "")
        }
    }
}
