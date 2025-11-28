package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.domain.usecase.GetDataUseCase
import com.covid.tqp.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Representa el estado de los datos de un país en la pantalla de comparación.
 *
 * @property countryName El nombre del país.
 * @property isLoading `true` si los datos se están cargando.
 * @property dataForSelectedDate Resumen de datos para la fecha actualmente seleccionada.
 * @property cachedCasesData Mapa con todos los datos históricos de casos.
 * @property cachedDeathsData Mapa con todos los datos históricos de muertes.
 */
data class ComparisonCountryData(
    val countryName: String,
    val isLoading: Boolean = true,
    val dataForSelectedDate: DailyDataSummary? = null,
    val cachedCasesData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap(),
    val cachedDeathsData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap()
)

/**
 * Define el estado de la UI para la pantalla de comparación.
 *
 * @property comparisonData Un mapa que asocia el nombre de un país con sus datos.
 * @property selectedDate La fecha seleccionada para mostrar los datos.
 * @property availableDates El conjunto de todas las fechas con datos disponibles.
 * @property error Un mensaje de error general.
 */
data class ComparisonUiState(
    val comparisonData: Map<String, ComparisonCountryData> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val availableDates: Set<LocalDate> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val countriesArg = savedStateHandle.get<String>(AppDestinations.COMPARISON_ARGUMENT) ?: ""
        val countries = countriesArg.split(',').filter { it.isNotBlank() }

        val initialData = countries.associateWith { ComparisonCountryData(countryName = it) }
        _uiState.value = ComparisonUiState(comparisonData = initialData)

        // Orquesta la carga de datos y el cálculo de fechas de forma secuencial.
        viewModelScope.launch {
            // Lanza todos los trabajos de obtención de datos en paralelo y los recolecta.
            val jobs = countries.map { countryName ->
                launch {
                    fetchDataForCountry(countryName)
                }
            }
            // Espera a que todos los trabajos terminen antes de continuar.
            jobs.joinAll()

            // Una vez que todos los datos han sido cargados, calcula las fechas disponibles.
            updateAvailableDatesAndSetLatest()
        }
    }

    private suspend fun fetchDataForCountry(countryName: String) {
        try {
            val casesResponses = getDataUseCase(country = countryName, type = "cases")
            val deathsResponses = getDataUseCase(country = countryName, type = "deaths")

            val aggregatedCases = casesResponses.flatMap { it.cases?.toList() ?: emptyList() }.toMap()
            val aggregatedDeaths = deathsResponses.flatMap { it.deaths?.toList() ?: emptyList() }.toMap()

            _uiState.update { currentState ->
                val updatedData = currentState.comparisonData.toMutableMap()
                updatedData[countryName] = currentState.comparisonData[countryName]?.copy(
                    isLoading = false,
                    cachedCasesData = aggregatedCases,
                    cachedDeathsData = aggregatedDeaths
                ) ?: ComparisonCountryData(countryName)
                currentState.copy(comparisonData = updatedData)
            }
        } catch (e: Exception) {
            _uiState.update { currentState ->
                val updatedData = currentState.comparisonData.toMutableMap()
                updatedData[countryName] = currentState.comparisonData[countryName]?.copy(isLoading = false)
                    ?: ComparisonCountryData(countryName)
                currentState.copy(
                    comparisonData = updatedData,
                    error = "Error al cargar datos para $countryName"
                )
            }
        }
    }

    private fun updateAvailableDatesAndSetLatest() {
        val allDates = _uiState.value.comparisonData.values
            .flatMap { it.cachedCasesData.keys + it.cachedDeathsData.keys }
            .mapNotNull { runCatching { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }.getOrNull() }
            .toSet()

        val latestDate = allDates.maxOrNull() ?: LocalDate.now()
        _uiState.update { it.copy(availableDates = allDates, selectedDate = latestDate) }
        updateDataForSelectedDate(latestDate)
    }


    fun onDateSelected(newDate: LocalDate) {
        _uiState.update { it.copy(selectedDate = newDate) }
        updateDataForSelectedDate(newDate)
    }

    private fun updateDataForSelectedDate(date: LocalDate) {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        _uiState.update { currentState ->
            val newComparisonData = currentState.comparisonData.mapValues { (_, countryData) ->
                val summary = extractDailyDataSummary(formattedDate, countryData)
                countryData.copy(dataForSelectedDate = summary)
            }
            currentState.copy(comparisonData = newComparisonData)
        }
    }

    private fun extractDailyDataSummary(formattedDate: String, countryData: ComparisonCountryData): DailyDataSummary? {
        val cases = countryData.cachedCasesData[formattedDate]
        val deaths = countryData.cachedDeathsData[formattedDate]
        return if (cases != null || deaths != null) {
            DailyDataSummary(
                totalCases = cases?.total ?: 0, newCases = cases?.new ?: 0,
                totalDeaths = deaths?.total ?: 0, newDeaths = deaths?.new ?: 0
            )
        } else null
    }
}