package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

/**
 * Representa el estado de la UI para la pantalla de detalle del país.
 *
 * @property countryName El nombre del país que se está mostrando.
 * @property isLoading `true` si los datos se están cargando o actualizando, `false` en caso contrario.
 * @property selectedDate La fecha actualmente seleccionada por el usuario.
 * @property dataForSelectedDate Un resumen de los datos de COVID para la [selectedDate].
 * @property availableDates Un conjunto de todas las fechas para las que hay datos disponibles.
 * @property error Un mensaje de error si la carga falló.
 */
data class CountryDetailUiState(
    val countryName: String,
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val dataForSelectedDate: DailyDataSummary? = null,
    val availableDates: Set<LocalDate> = emptySet(),
    val error: String? = null
)

/**
 * Contiene un resumen de los datos diarios de COVID, combinando casos y muertes.
 *
 * @property totalCases El número total acumulado de casos.
 * @property newCases El número de casos nuevos reportados.
 * @property totalDeaths El número total acumulado de muertes.
 * @property newDeaths El número de muertes nuevas reportadas.
 */
data class DailyDataSummary(
    val totalCases: Int,
    val newCases: Int,
    val totalDeaths: Int,
    val newDeaths: Int
)

/**
 * [ViewModel] para la pantalla de detalle del país ([CountryDetailScreen]).
 *
 * Gestiona la obtención, el almacenamiento en caché y la presentación de los datos de COVID
 * para un país específico, permitiendo al usuario ver los datos por fecha.
 *
 * @param getDataUseCase El caso de uso para obtener los datos de COVID.
 * @param savedStateHandle Manejador para acceder a los argumentos de navegación guardados.
 */
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
    /**
     * El flujo de estado de la UI para la pantalla de detalle, observado por la vista.
     */
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    private var cachedCasesData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap()
    private var cachedDeathsData: Map<String, com.covid.tqp.data.model.DailyData> = emptyMap()

    init {
        fetchCountryData()
    }

    /**
     * Inicia la obtención y procesamiento de todos los datos de casos y muertes para el país.
     *
     * Realiza llamadas separadas para "cases" y "deaths", agrega los resultados,
     * y los almacena en caché. Luego, actualiza el [uiState] con las fechas disponibles
     * y los datos para la fecha más reciente.
     */
    private fun fetchCountryData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val casesResponses = getDataUseCase(country = _countryName, type = "cases")
                val deathsResponses = getDataUseCase(country = _countryName, type = "deaths")

                val aggregatedCases = mutableMapOf<String, com.covid.tqp.data.model.DailyData>()
                casesResponses.forEach { response ->
                    response.cases?.forEach { (date, dailyData) ->
                        aggregatedCases.merge(date, dailyData) { old, new ->
                            com.covid.tqp.data.model.DailyData(total = old.total + new.total, new = old.new + new.new)
                        }
                    }
                }
                cachedCasesData = aggregatedCases

                val aggregatedDeaths = mutableMapOf<String, com.covid.tqp.data.model.DailyData>()
                deathsResponses.forEach { response ->
                    response.deaths?.forEach { (date, dailyData) ->
                        aggregatedDeaths.merge(date, dailyData) { old, new ->
                            com.covid.tqp.data.model.DailyData(total = old.total + new.total, new = old.new + new.new)
                        }
                    }
                }
                cachedDeathsData = aggregatedDeaths

                val allDates = (cachedCasesData.keys + cachedDeathsData.keys)
                    .mapNotNull { runCatching { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }.getOrNull() }
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

    /**
     * Se llama cuando el usuario selecciona una nueva fecha en la UI.
     * @param newDate La nueva fecha seleccionada.
     */
    fun onDateSelected(newDate: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = newDate)
        updateDataForSelectedDate(newDate)
    }

    /**
     * Actualiza el [uiState] con los datos correspondientes a una fecha recién seleccionada.
     *
     * Simula un pequeño retraso para mejorar la experiencia del usuario mostrando un indicador de carga.
     * @param date La fecha para la que se deben mostrar los datos.
     */
    private fun updateDataForSelectedDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(300L) // Retraso artificial para que la UI pueda renderizar el estado de carga
            val data = extractDailyDataSummary(date)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dataForSelectedDate = data,
                error = if (data == null) "No hay datos disponibles para esta fecha." else null
            )
        }
    }

    /**
     * Extrae y combina los datos de casos y muertes de la caché para una fecha específica.
     *
     * @param date La fecha para la que se deben extraer los datos.
     * @return Un [DailyDataSummary] si se encuentran datos de casos o muertes, `null` en caso contrario.
     */
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