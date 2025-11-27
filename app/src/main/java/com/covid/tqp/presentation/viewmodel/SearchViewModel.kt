package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.data.source.local.UserPreferencesRepository
import com.covid.tqp.domain.usecase.GetDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val countryName: String, val data: CovidDataResponse?) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    init {
        loadLastViewedCountry()
    }

    private fun loadLastViewedCountry() {
        viewModelScope.launch {
            userPreferencesRepository.lastViewedCountry.collect { country ->
                country?.let { _searchQuery.value = it }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun searchCountry() {
        val country = _searchQuery.value.trim()
        if (country.isEmpty()) {
            _searchUiState.value = SearchUiState.Error("Por favor, introduce un nombre de país.")
            return
        }

        _searchUiState.value = SearchUiState.Loading
        viewModelScope.launch {
            try {
                val result = getDataUseCase(country = country).firstOrNull()
                if (result != null) {
                    userPreferencesRepository.saveLastViewedCountry(country) // Guardar país preferido
                    _searchUiState.value = SearchUiState.Success(country, result)
                } else {
                    _searchUiState.value = SearchUiState.Error("No se encontraron datos para \"$country\".")
                }
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error("Error al buscar \"$country\": ${e.localizedMessage ?: "Error desconocido"}")
            }
        }
    }

    fun resetSearchState() {
        _searchUiState.value = SearchUiState.Idle
    }
}