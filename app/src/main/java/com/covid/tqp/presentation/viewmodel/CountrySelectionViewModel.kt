package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Define el estado de la UI para la pantalla de selección de países.
 *
 * @property countries La lista de todos los países disponibles para la selección.
 * @property selectedCountries El conjunto de países que el usuario ha seleccionado.
 * @property isLoading `true` si la lista de países se está cargando.
 */
data class CountrySelectionUiState(
    val countries: List<String> = emptyList(),
    val selectedCountries: Set<String> = emptySet(),
    val isLoading: Boolean = true
)

/**
 * [ViewModel] para la pantalla [CountrySelectionScreen].
 *
 * Gestiona la lista de países y la selección del usuario.
 */
@HiltViewModel
class CountrySelectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CountrySelectionUiState())
    val uiState = _uiState.asStateFlow()

    // Lista de países hardcodeada para simplificar.
    private val allCountries = listOf(
        "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Australia", "Austria", "Bahamas", "Bangladesh",
        "Belarus", "Belgium", "Bolivia", "Brazil", "Bulgaria", "Cambodia", "Cameroon", "Canada", "Chile", "China", "Colombia",
        "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czechia", "Denmark", "Dominican Republic", "Ecuador", "Egypt", "Estonia",
        "Finland", "France", "Germany", "Greece", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
        "Ireland", "Israel", "Italy", "Japan", "Kazakhstan", "Kenya", "Kuwait", "Latvia", "Lebanon", "Libya", "Lithuania",
        "Luxembourg", "Malaysia", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Nepal", "Netherlands",
        "New Zealand", "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan", "Panama", "Paraguay", "Peru", "Philippines",
        "Poland", "Portugal", "Qatar", "Romania", "Russia", "Saudi Arabia", "Senegal", "Serbia", "Singapore", "Slovakia",
        "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "Sweden", "Switzerland", "Taiwan", "Tanzania",
        "Thailand", "Tunisia", "Turkey", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "Uruguay", "US", "Venezuela"
    )

    init {
        loadCountries()
    }

    /**
     * Simula la carga asíncrona de la lista de países para evitar bloquear la UI.
     */
    private fun loadCountries() {
        viewModelScope.launch {
            delay(500) // Simula una pequeña demora de red o procesamiento.
            _uiState.update {
                it.copy(
                    countries = allCountries.sorted(),
                    isLoading = false
                )
            }
        }
    }

    /**
     * Alterna la selección de un país.
     *
     * Si el país ya está seleccionado, lo elimina de la selección. Si no lo está, lo añade.
     *
     * @param countryName El nombre del país a seleccionar/deseleccionar.
     */
    fun toggleCountrySelection(countryName: String) {
        _uiState.update { currentState ->
            val newSelection = currentState.selectedCountries.toMutableSet()
            if (newSelection.contains(countryName)) {
                newSelection.remove(countryName)
            } else {
                newSelection.add(countryName)
            }
            currentState.copy(selectedCountries = newSelection)
        }
    }
}