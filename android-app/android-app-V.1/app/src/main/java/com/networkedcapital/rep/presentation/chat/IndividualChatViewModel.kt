package com.networkedcapital.rep.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.MessageRepository
import com.networkedcapital.rep.domain.model.MessageModel
import com.networkedcapital.rep.utils.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class IndividualChatUiState(
    val messages: List<MessageModel> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isLoadingOlder: Boolean = false,
    val canLoadOlder: Boolean = true,
    val isInitialized: Boolean = false,
    val error: String? = null
)

/**
 * IndividualChatViewModel - Direct message chat
 * Based on iOS MessageViewModel from Chat_Individual.swift
 */
@HiltViewModel
class IndividualChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndividualChatUiState())
    val uiState: StateFlow<IndividualChatUiState> = _uiState

    private var socketObserverId: UUID? = null
    private var isFetchingMessages = false
    private var isSettingUpSocket = false

    var currentUserId: Int = 0
        private set
    var otherUserId: Int = 0
        private set

    fun initialize(currentUserId: Int, otherUserId: Int) {
        this.currentUserId = currentUserId
        this.otherUserId = otherUserId

        android.util.Log.d("IndividualChatVM", "✅ INIT for otherUserId: $otherUserId")

        // Start initial fetch
        fetchMessages()

        // Delay socket setup slightly to avoid deadlock (iOS does 1.2s delay)
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            setupSocketListener()
        }
    }

    private fun setupSocketListener() {
        android.util.Log.d("IndividualChatVM", "📞 Attempting to setup socket listener...")

        if (isSettingUpSocket) {
            android.util.Log.w("IndividualChatVM", "⚠️ Aborted: setupSocketListener() already in progress")
            return
        }

        isSettingUpSocket = true

        // Remove existing listener first
        socketObserverId?.let { id ->
            android.util.Log.d("IndividualChatVM", "   Removing existing socket observer: $id")
            SocketManager.removeDirectMessageObserver(id)
        }

        // Register new listener
        socketObserverId = SocketManager.onDirectMessageNotification { payload ->
            android.util.Log.d("IndividualChatVM", "📡 Received socket payload: $payload")

            val senderId = (payload["sender_id"] as? Number)?.toInt()
                         ?: (payload["senderId"] as? Number)?.toInt()
            val recipientId = (payload["recipient_id"] as? Number)?.toInt()
                           ?: (payload["recipientId"] as? Number)?.toInt()

            // Only process if from the other user to me
            if (senderId == otherUserId && recipientId == currentUserId) {
                android.util.Log.d("IndividualChatVM", "   Payload is relevant. Processing...")

                try {
                    val message = MessageModel(
                        id = (payload["id"] as? Number)?.toInt() ?: (payload["message_id"] as? Number)?.toInt() ?: 0,
                        senderId = senderId ?: 0,
                        senderName = payload["sender_name"] as? String,
                        recipientId = recipientId,
                        text = payload["text"] as? String ?: "",
                        timestamp = payload["timestamp"] as? String ?: "",
                        read = payload["read"] as? String
                    )

                    appendIfNeeded(message)
                } catch (e: Exception) {
                    android.util.Log.e("IndividualChatVM", "Failed to parse message: ${e.message}")
                }
            } else {
                android.util.Log.d("IndividualChatVM", "   Payload ignored (not for this conversation)")
            }
        }

        android.util.Log.d("IndividualChatVM", "👍 Socket listener setup complete. Observer ID: $socketObserverId")
        isSettingUpSocket = false
    }

    private fun appendIfNeeded(message: MessageModel) {
        _uiState.update { state ->
            if (!state.messages.any { it.id == message.id }) {
                android.util.Log.d("IndividualChatVM", "   Appending new message ID: ${message.id}")
                val newMessages = (state.messages + message).sortedBy { it.timestamp }
                state.copy(messages = newMessages)
            } else {
                android.util.Log.d("IndividualChatVM", "   Ignoring duplicate message ID: ${message.id}")
                state
            }
        }
    }

    fun fetchMessages() {
        fetchMessages(beforeId = null, append = false)
    }

    private fun fetchMessages(beforeId: Int? = null, append: Boolean) {
        android.util.Log.d("IndividualChatVM", "⬇️ fetchMessages called. Append: $append, BeforeID: ${beforeId ?: -1}")

        if (append) {
            if (_uiState.value.isLoadingOlder || !_uiState.value.canLoadOlder) {
                android.util.Log.w("IndividualChatVM", "⚠️ Aborted paginated fetch")
                return
            }
            _uiState.update { it.copy(isLoadingOlder = true) }
        } else {
            if (isFetchingMessages) {
                android.util.Log.w("IndividualChatVM", "⚠️ Aborted initial fetch: Another fetch is already in progress")
                return
            }
            isFetchingMessages = true
            _uiState.update { it.copy(isLoading = true) }
        }

        viewModelScope.launch {
            messageRepository.getDirectMessages(
                otherUserId = otherUserId,
                beforeId = beforeId,
                markAsRead = !append
            ).collect { result ->
                _uiState.update { state ->
                    if (append) {
                        state.copy(isLoadingOlder = false)
                    } else {
                        isFetchingMessages = false
                        state.copy(isLoading = false)
                    }
                }

                result.onSuccess { newMsgs ->
                    android.util.Log.d("IndividualChatVM", "✅ Fetch successful. Received ${newMsgs.size} messages")
                    val sortedMsgs = newMsgs.sortedBy { it.timestamp }

                    _uiState.update { state ->
                        if (append) {
                            if (sortedMsgs.isEmpty()) {
                                android.util.Log.d("IndividualChatVM", "   No older messages found. Disabling pagination")
                                state.copy(canLoadOlder = false)
                            } else {
                                val existingIds = state.messages.map { it.id }.toSet()
                                val filtered = sortedMsgs.filter { !existingIds.contains(it.id) }
                                android.util.Log.d("IndividualChatVM", "   Prepending ${filtered.count()} older messages")
                                state.copy(messages = filtered + state.messages)
                            }
                        } else {
                            state.copy(
                                messages = sortedMsgs,
                                isInitialized = true
                            )
                        }
                    }
                }.onFailure { error ->
                    android.util.Log.e("IndividualChatVM", "❌ Message fetch error: ${error.message}")
                    _uiState.update { state ->
                        if (!append) {
                            state.copy(
                                messages = emptyList(),
                                isInitialized = true,
                                error = error.message
                            )
                        } else {
                            state.copy(error = error.message)
                        }
                    }
                }
            }
        }
    }

    fun loadOlderIfNeeded(firstVisibleId: Int?) {
        val messages = _uiState.value.messages
        if (messages.isEmpty()) return

        firstVisibleId?.let { id ->
            if (messages.firstOrNull()?.id == id) {
                android.util.Log.d("IndividualChatVM", "🔝 Top of list reached, loading older messages")
                fetchMessages(beforeId = id, append = true)
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val trimmed = _uiState.value.inputText.trim()
        if (trimmed.isEmpty()) return

        android.util.Log.d("IndividualChatVM", "⬆️ Sending message...")

        viewModelScope.launch {
            messageRepository.sendDirectMessage(
                otherUserId = otherUserId,
                message = trimmed
            ).collect { result ->
                result.onSuccess { sentMessage ->
                    android.util.Log.d("IndividualChatVM", "✅ Send message successful")
                    appendIfNeeded(sentMessage)
                    _uiState.update { it.copy(inputText = "") }
                }.onFailure { error ->
                    android.util.Log.e("IndividualChatVM", "❌ Send message error: ${error.message}")
                    _uiState.update { it.copy(error = error.message) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        android.util.Log.d("IndividualChatVM", "🗑️ DEINIT for otherUserId: $otherUserId")

        socketObserverId?.let { id ->
            android.util.Log.d("IndividualChatVM", "   Removing socket observer $id")
            SocketManager.removeDirectMessageObserver(id)
        }

        socketObserverId = null
        isFetchingMessages = false
        isSettingUpSocket = false
    }
}
