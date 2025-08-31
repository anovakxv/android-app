package com.networkedcapital.rep.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.presentation.chat.SimpleMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class IndividualChatUiState(
    val messages: List<SimpleMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class IndividualChatViewModel(
    private val otherUserId: Int,
    private val currentUserId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(IndividualChatUiState())
    val uiState: StateFlow<IndividualChatUiState> = _uiState

    fun loadMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                // TODO: Replace with real API call
                val messages = listOf<SimpleMessage>() // Fetch messages
                _uiState.value = _uiState.value.copy(messages = messages, isLoading = false)
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
