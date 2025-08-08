package com.networkedcapital.rep.presentation.portal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.networkedcapital.rep.data.repository.PortalRepository
import com.networkedcapital.rep.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortalDetailViewModel @Inject constructor(
    private val portalRepository: PortalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortalDetailUiState())
    val uiState: StateFlow<PortalDetailUiState> = _uiState.asStateFlow()

    fun loadPortalDetail(portalId: Int, userId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Load portal detail
                portalRepository.getPortalDetail(portalId, userId).collect { result ->
                    result.fold(
                        onSuccess = { portalDetail ->
                            _uiState.update { 
                                it.copy(
                                    portalDetail = portalDetail,
                                    isLoading = false
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error.message
                                )
                            }
                        }
                    )
                }

                // Load portal goals
                portalRepository.getPortalGoals(portalId).collect { result ->
                    result.fold(
                        onSuccess = { goals ->
                            _uiState.update { it.copy(portalGoals = goals) }
                        },
                        onFailure = { error ->
                            // Handle error if needed
                        }
                    )
                }

                // Load reporting increments if needed
                if (_uiState.value.reportingIncrements.isEmpty()) {
                    portalRepository.getReportingIncrements().collect { result ->
                        result.fold(
                            onSuccess = { increments ->
                                _uiState.update { it.copy(reportingIncrements = increments) }
                            },
                            onFailure = { error ->
                                // Handle error if needed
                            }
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun selectSection(sectionIndex: Int) {
        _uiState.update { it.copy(selectedSection = sectionIndex) }
    }

    fun flagPortal(portalId: Int, reason: String = "") {
        viewModelScope.launch {
            try {
                portalRepository.flagPortal(portalId, reason).collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.update { 
                                it.copy(
                                    flagResult = "Portal flagged. Thank you for your report."
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update { 
                                it.copy(
                                    flagResult = error.message ?: "Failed to flag portal."
                                )
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        flagResult = e.message ?: "Failed to flag portal."
                    )
                }
            }
        }
    }

    fun clearFlagResult() {
        _uiState.update { it.copy(flagResult = null) }
    }
}

data class PortalDetailUiState(
    val isLoading: Boolean = false,
    val portalDetail: PortalDetail? = null,
    val portalGoals: List<Goal> = emptyList(),
    val reportingIncrements: List<ReportingIncrement> = emptyList(),
    val selectedSection: Int = 0, // 0 = Goal Teams, 1 = Story
    val errorMessage: String? = null,
    val flagResult: String? = null
)
