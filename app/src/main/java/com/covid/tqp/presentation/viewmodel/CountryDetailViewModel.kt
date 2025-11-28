package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.usecase.GetDataUseCase
import com.covid.tqp.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CountryDetailUiState(
    val countryName: String,
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val dataForSelectedDate: DailyDataSummary? = null,
    val availableDates: Set<LocalDate> = emptySet(),
    val error: String? = null
)

data class DailyDataSummary(
    val totalCases: Int,
    val newCases: Int,
    val totalDeaths: Int,
    val newDeaths: Int
)

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _countryName: String = savedStateHandle.get<String>(AppDestinations.COUNTRY_DETAIL_ARGUMENT)
        ?: throw IllegalArgumentException("Country Name argument missing")

    private val _uiState = MutableStateFlow(
        CountryDetailUiState(countryName = _countryName, isLoading = true)
    )
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    private var cachedCasesData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap()
    private var cachedDeathsData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap()

    init {
        fetchCountryData()
    }

    private fun fetchCountryData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Realizar dos llamadas separadas: una para casos y otra para muertes
                // y obtener *todas* las respuestas, no solo la primera
                val casesResponses = getDataUseCase(country = _countryName, type = "cases")
                val deathsResponses = getDataUseCase(country = _countryName, type = "deaths")

                val aggregatedCases = mutableMapOf<String, com.covid.tqp.data.model.DailyData>()
                casesResponses.forEach { response ->
                    response.cases?.let { casesMap ->
                        casesMap.forEach { (date, dailyData) ->
                            aggregatedCases.merge(date, dailyData) { old, new ->
                                // Simple merge: si hay duplicados, sumamos los totales y nuevos
                                com.covid.tqp.data.model.DailyData(total = old.total + new.total, new = old.new + new.new)
                            }
                        }
                    }
                }
                cachedCasesData = aggregatedCases

                val aggregatedDeaths = mutableMapOf<String, com.covid.tqp.data.model.DailyData>()
                deathsResponses.forEach { response ->
                    response.deaths?.let { deathsMap ->
                        deathsMap.forEach { (date, dailyData) ->
                            aggregatedDeaths.merge(date, dailyData) { old, new ->
                                // Simple merge: si hay duplicados, sumamos los totales y nuevos
                                com.covid.tqp.data.model.DailyData(total = old.total + new.total, new = old.new + new.new)
                            }
                        }
                    }
                }
                cachedDeathsData = aggregatedDeaths

                val allDates = (cachedCasesData.keys + cachedDeathsData.keys)
                    .map { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
                    .toSet()

                val latestDateWithData = allDates.maxOrNull() ?: LocalDate.now()

                val dataForLatestDate = extractDailyDataSummary(latestDateWithData)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    availableDates = allDates,
                    selectedDate = latestDateWithData,
                    dataForSelectedDate = dataForLatestDate,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos para ${_countryName}: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    fun onDateSelected(newDate: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
        updateDataForSelectedDate(newDate)
    }

    private fun updateDataForSelectedDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true) // <-- Se establece isLoading en true
            delay(300L) // <-- Retraso artificial para que la UI pueda renderizar el estado de carga
            val data = extractDailyDataSummary(date)
            _uiState.value = _uiState.value.copy(
                isLoading = false, // <-- Se establece isLoading en false después del retraso
                dataForSelectedDate = data,
                error = if (data == null) "No hay datos disponibles para esta fecha." else null
            )
        }
    }

    private fun extractDailyDataSummary(date: LocalDate): DailyDataSummary? {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        val cases = cachedCasesData[formattedDate]
        val deaths = cachedDeathsData[formattedDate]

        return if (cases != null || deaths != null) {
            DailyDataSummary(
                totalCases = cases?.total ?: 0,
                newCases = cases?.new ?: 0,
                totalDeaths = deaths?.total ?: 0,
                newDeaths = deaths?.new ?: 0
            )
        } else {
            null
        }
    }
}