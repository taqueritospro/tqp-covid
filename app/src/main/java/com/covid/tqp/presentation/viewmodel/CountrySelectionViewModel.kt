package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Define el estado de la UI para la pantalla de selección de países.
 *
 * @property countries La lista de todos los países disponibles para la selección.
 * @property selectedCountries El conjunto de países que el usuario ha seleccionado.
 */
data class CountrySelectionUiState(
    val countries: List<String> = emptyList(),
    val selectedCountries: Set<String> = emptySet()
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

    // Lista de países hardcodeada para simplificar. En una app real, vendría de un repositorio.
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
        _uiState.value = CountrySelectionUiState(countries = allCountries.sorted())
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