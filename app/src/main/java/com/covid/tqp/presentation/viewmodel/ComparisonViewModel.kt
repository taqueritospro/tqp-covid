package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.usecase.GetDataUseCase
import com.covid.tqp.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa el estado de los datos de un país en la pantalla de comparación.
 *
 * @property countryName El nombre del país.
 * @property data Los datos de COVID-19 para el país. Nulo si está cargando o hubo un error.
 * @property isLoading `true` si los datos se están cargando.
 */
data class ComparisonCountryData(
    val countryName: String,
    val data: CovidDataResponse? = null,
    val isLoading: Boolean = true
)

/**
 * Define el estado de la UI para la pantalla de comparación.
 *
 * @property comparisonData Un mapa que asocia el nombre de un país con sus datos de comparación.
 * @property error Un mensaje de error general si algo sale mal.
 */
data class ComparisonUiState(
    val comparisonData: Map<String, ComparisonCountryData> = emptyMap(),
    val error: String? = null
)

/**
 * [ViewModel] para la pantalla [ComparisonScreen].
 *
 * Recibe una lista de países a través de [SavedStateHandle], obtiene los datos para cada uno
 * y prepara el estado para que la UI los muestre.
 *
 * @param getDataUseCase El caso de uso para obtener los datos de COVID.
 * @param savedStateHandle Manejador para acceder a los argumentos de navegación.
 */
@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Obtiene la lista de países del argumento de navegación.
        val countriesArg = savedStateHandle.get<String>(AppDestinations.COMPARISON_ARGUMENT) ?: ""
        val countries = countriesArg.split(',').filter { it.isNotBlank() }

        // Inicializa el estado con todos los países en estado de carga.
        val initialData = countries.associateWith { ComparisonCountryData(countryName = it) }
        _uiState.value = ComparisonUiState(comparisonData = initialData)

        // Lanza una corutina para buscar los datos de cada país.
        countries.forEach { countryName ->
            fetchDataForCountry(countryName)
        }
    }

    private fun fetchDataForCountry(countryName: String) {
        viewModelScope.launch {
            try {
                // Busca los datos. Usamos firstOrNull para simplificar, asumiendo una respuesta por país.
                val result = getDataUseCase(country = countryName).firstOrNull()
                updateCountryData(countryName, result, false)
            } catch (e: Exception) {
                // En caso de error, actualiza el estado para reflejarlo.
                updateCountryData(countryName, null, false)
                _uiState.value = _uiState.value.copy(error = "Error al cargar datos para $countryName")
            }
        }
    }

    /**
     * Actualiza el estado de la UI con los datos de un país específico.
     */
    private fun updateCountryData(countryName: String, data: CovidDataResponse?, isLoading: Boolean) {
        val currentData = _uiState.value.comparisonData.toMutableMap()
        currentData[countryName] = ComparisonCountryData(countryName, data, isLoading)
        _uiState.value = _uiState.value.copy(comparisonData = currentData)
    }
}