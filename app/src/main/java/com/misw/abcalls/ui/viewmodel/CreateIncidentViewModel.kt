package com.misw.abcalls.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateIncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateIncidentUiState>(CreateIncidentUiState.Initial)
    val uiState: StateFlow<CreateIncidentUiState> = _uiState

    fun createIncident(name: String, description: String, attachmentUrl: String?) {
        viewModelScope.launch {
            _uiState.value = CreateIncidentUiState.Loading
            val result = incidentRepository.createIncident(
                Incident(name = name, description = description, attachmentUrl = attachmentUrl)
            )
            _uiState.value = when {
                result.isSuccess -> CreateIncidentUiState.Success(result.getOrNull()!!)
                else -> CreateIncidentUiState.Error(result.exceptionOrNull()?.message ?: "Error!")
            }
        }
    }
}

sealed class CreateIncidentUiState {
    object Initial : CreateIncidentUiState()
    object Loading : CreateIncidentUiState()
    data class Success(val incident: Incident) : CreateIncidentUiState()
    data class Error(val message: String) : CreateIncidentUiState()
}