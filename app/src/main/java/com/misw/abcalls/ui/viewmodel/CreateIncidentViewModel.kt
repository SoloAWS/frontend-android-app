package com.misw.abcalls.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class CreateIncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateIncidentUiState())
    val uiState: StateFlow<CreateIncidentUiState> = _uiState

    fun loadCompanies(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val companyResponse = incidentRepository.getCompanies(userId)
                _uiState.update {
                    it.copy(companies = companyResponse.companies, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Error loading companies", isLoading = false)
                }
            }
        }
    }

    fun createIncident(description: String, companyId: String, userId: String, fileUri: Uri?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val incident = incidentRepository.createIncident(description, companyId, userId, fileUri)
                _uiState.update { it.copy(createdIncident = incident, isLoading = false) }
            } catch (e: Exception) {

                e.message?.let { Log.e("API response", it) }
                _uiState.update { it.copy(error = e.message ?: "Error creating incident", isLoading = false) }
            }
        }
    }

    fun resetState() {
        _uiState.update { currentState ->
            currentState.copy(
                createdIncident = null,
                error = null
            )
        }
    }
}

data class CreateIncidentUiState(
    val companies: List<Company> = emptyList(),
    val createdIncident: Incident? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)