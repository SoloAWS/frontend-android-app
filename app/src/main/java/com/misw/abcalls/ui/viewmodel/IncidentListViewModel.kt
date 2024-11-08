package com.misw.abcalls.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentListViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository
) : ViewModel() {
    data class IncidentListUiState(
        val incidents: List<Incident> = emptyList(),
        val filteredIncidents: List<Incident> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = ""
    )

    private val _shouldRefresh = MutableStateFlow(true)
    private val _uiState = MutableStateFlow(IncidentListUiState())
    val uiState: StateFlow<IncidentListUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var searchJob: Job? = null
    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            _shouldRefresh
                .filter { it }
                .collect {
                    _shouldRefresh.value = false
                }
        }
    }
    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = incidentRepository.getUserIncidents()
                result.fold(
                    onSuccess = { incidents ->
                        Log.d("IncidentListViewModel", "Received ${incidents.size} incidents")
                        Log.d("IncidentListViewModel", "Current state incidents: ${_uiState.value.incidents.size}")
                        val sortedIncidents = incidents.sortedByDescending { it.creation_date }
                        _uiState.update { state ->
                            state.copy(
                                incidents = sortedIncidents,
                                filteredIncidents = sortedIncidents,
                                isLoading = false,
                                error = null,
                                searchQuery = state.searchQuery // Preserve search query
                            )
                        }
                        Log.d("IncidentListViewModel", "Updated state incidents: ${_uiState.value.incidents.size}")
                    },
                    onFailure = { error ->
                        Log.e("IncidentListViewModel", "Error updating incidents", error)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Error de conexión. Por favor intenta más tarde"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("IncidentListViewModel", "Exception updating incidents", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión. Por favor intenta más tarde"
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            delay(300) // Debounce search
            filterIncidents()
        }
    }

    private fun filterIncidents() {
        val query = _uiState.value.searchQuery
        _uiState.update { currentState ->
            currentState.copy(
                filteredIncidents = currentState.incidents.filter {
                    it.description.contains(query, ignoreCase = true) ||
                            it.state.contains(query, ignoreCase = true) ||
                            it.priority.contains(query, ignoreCase = true)
                }
            )
        }
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = incidentRepository.getUserIncidents()

                result.fold(
                    onSuccess = { incidents ->
                        _uiState.update { state ->
                            state.copy(
                                incidents = incidents.sortedByDescending { it.creation_date },
                                filteredIncidents = incidents.sortedByDescending { it.creation_date },
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { _ ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Error de conexión. Por favor intenta más tarde"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión. Por favor intenta más tarde"
                    )
                }
            }
        }
    }

    fun retryLoading() {
        loadIncidents()
    }
}