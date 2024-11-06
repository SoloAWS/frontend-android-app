package com.misw.abcalls.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(IncidentListUiState())
    val uiState: StateFlow<IncidentListUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadIncidents()

        _searchQuery
            .debounce(300)
            .onEach { query ->
                _uiState.update { currentState ->
                    currentState.copy(
                        filteredIncidents = currentState.incidents.filter {
                            it.name.contains(query, ignoreCase = true)
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Replace with actual API call
                // Simulating API delay
                kotlinx.coroutines.delay(1000)
                val mockIncidents = listOf(
                    IncidentItem("Problema de conexión", "Alto", "En progreso", "2024-03-01"),
                    IncidentItem("Error en factura", "Medio", "Pendiente", "2024-02-28"),
                    IncidentItem("Servicio intermitente", "Bajo", "Resuelto", "2024-02-27")
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        incidents = mockIncidents,
                        filteredIncidents = mockIncidents
                    )
                }
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
        _uiState.update { it.copy(error = null) }
        loadIncidents()
    }
}

data class IncidentItem(
    val name: String,
    val riskLevel: String,
    val status: String,
    val creationDate: String
)

data class IncidentListUiState(
    val incidents: List<IncidentItem> = emptyList(),
    val filteredIncidents: List<IncidentItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)