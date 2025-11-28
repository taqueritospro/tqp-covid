package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.usecase.GetDataUseCase
import com.covid.tqp.navigation.AppDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        fetchCountryData()
    }

    private fun fetchCountryData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Obtenemos todos los datos disponibles para el país sin fecha específica inicialmente
                val response = getDataUseCase(country = _countryName).firstOrNull()

                if (response != null) {
                    val casesMap = response.cases ?: emptyMap()
                    val deathsMap = response.deaths ?: emptyMap()

                    val allDates = (casesMap.keys + deathsMap.keys).map { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }.toSet()

                    val latestDateWithData = allDates.maxOrNull() ?: LocalDate.now()

                    val dataForLatestDate = extractDailyDataSummary(casesMap, deathsMap, latestDateWithData)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableDates = allDates,
                        selectedDate = latestDateWithData,
                        dataForSelectedDate = dataForLatestDate,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontraron datos para ${_countryName}."
                    )
                }
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
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = getDataUseCase(country = _countryName).firstOrNull() // Volvemos a obtener todos los datos o los guardamos en caché

                if (response != null) {
                    val casesMap = response.cases ?: emptyMap()
                    val deathsMap = response.deaths ?: emptyMap()

                    val data = extractDailyDataSummary(casesMap, deathsMap, date)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dataForSelectedDate = data,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, dataForSelectedDate = null, error = "No hay datos disponibles para esta fecha.")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    dataForSelectedDate = null,
                    error = "Error al obtener datos para la fecha: ${e.localizedMessage ?: "Error desconocido"}"
                )
            }
        }
    }

    private fun extractDailyDataSummary(
        casesMap: Map<String, com.covid.tqp.data.model.DailyData>,
        deathsMap: Map<String, com.covid.tqp.data.model.DailyData>,
        date: LocalDate
    ): DailyDataSummary? {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        val cases = casesMap[formattedDate]
        val deaths = deathsMap[formattedDate]

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