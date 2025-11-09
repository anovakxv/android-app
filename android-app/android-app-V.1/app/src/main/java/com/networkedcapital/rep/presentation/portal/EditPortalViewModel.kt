package com.networkedcapital.rep.presentation.portal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.PortalRepository
import com.networkedcapital.rep.domain.model.Portal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPortalUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class EditPortalViewModel @Inject constructor(
    private val portalRepository: PortalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPortalUiState())
    val uiState: StateFlow<EditPortalUiState> = _uiState.asStateFlow()

    fun createPortal(
        name: String,
        description: String,
        category: String = "General",
        location: String = "",
        isPrivate: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            portalRepository.createPortal(
                name = name,
                description = description,
                category = category,
                location = location,
                isPrivate = isPrivate
            ).collect { result ->
                result.fold(
                    onSuccess = { portal ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            successMessage = "Portal created successfully"
                        )
                        onSuccess()
                    },
                    onFailure = { throwable ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Failed to create portal"
                        )
                    }
                )
            }
        }
    }

    fun updatePortal(
        portalId: String,
        name: String,
        description: String,
        category: String = "General",
        location: String = "",
        isPrivate: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            portalRepository.updatePortal(
                portalId = portalId,
                name = name,
                description = description,
                category = category,
                location = location,
                isPrivate = isPrivate
            ).collect { result ->
                result.fold(
                    onSuccess = { portal ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            successMessage = "Portal updated successfully"
                        )
                        onSuccess()
                    },
                    onFailure = { throwable ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Failed to update portal"
                        )
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
