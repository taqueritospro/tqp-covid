package com.covid.tqp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.usecase.GetDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa el estado de la UI para un único país en la lista principal.
 *
 * @property countryName El nombre del país.
 * @property isLoading `true` si se están cargando los datos para este país, `false` en caso contrario.
 * @property data Los datos de COVID obtenidos para el país. Nulo si no se han cargado o hubo un error.
 * @property error Un mensaje de error si la carga falló. Nulo si la carga fue exitosa.
 */
data class CountryUiState(
    val countryName: String,
    val isLoading: Boolean = false,
    val data: CovidDataResponse? = null,
    val error: String? = null
)

/**
 * Define los posibles estados de la UI para la pantalla principal ([MainScreen]).
 */
sealed class MainScreenUiState {
    /**
     * Indica que la pantalla está en proceso de carga inicial.
     */
    object Loading : MainScreenUiState()

    /**
     * Indica que los datos de los países se han cargado con éxito.
     * @property countries La lista de estados para cada país a mostrar.
     */
    data class Success(val countries: List<CountryUiState>) : MainScreenUiState()

    /**
     * Indica que ocurrió un error general al cargar los datos.
     * @property message El mensaje de error a mostrar.
     */
    data class Error(val message: String) : MainScreenUiState()
}

/**
 * [ViewModel] para la pantalla principal ([MainScreen]).
 *
 * Gestiona el estado de la UI y la lógica de negocio para mostrar una lista de países predefinidos
 * y sus correspondientes datos de COVID.
 *
 * @param getDataUseCase El caso de uso para obtener los datos de COVID.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    /**
     * El flujo de estado de la UI para la pantalla principal, observado por la vista.
     */
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    /**
     * Lista de países predefinidos que se mostrarán en la pantalla principal.
     */
    val predefinedCountries = listOf(
        "Mexico", "Canada", "Honduras", "China", "Japan",
        "South Korea", "Finland", "Brazil", "France", "Argentina"
    )

    init {
        fetchInitialCountryData()
    }

    /**
     * Inicia la obtención de datos de COVID para la lista de países predefinidos.
     *
     * Actualiza el [uiState] progresivamente:
     * 1. Emite un estado [MainScreenUiState.Success] con todos los países en estado de carga (para mostrar shimmers).
     * 2. Itera sobre cada país, obtiene sus datos y actualiza su estado individual en la lista.
     * 3. Emite un nuevo estado [MainScreenUiState.Success] después de cada actualización de país.
     */
    private fun fetchInitialCountryData() {
        viewModelScope.launch {
            val initialCountryStates = predefinedCountries.map { CountryUiState(countryName = it, isLoading = true) }
            _uiState.value = MainScreenUiState.Success(initialCountryStates) // Mostrar shimmer inicialmente

            val updatedCountryStates = initialCountryStates.toMutableList()

            predefinedCountries.forEachIndexed { index, country ->
                try {
                    val data = getDataUseCase(country = country).firstOrNull()
                    updatedCountryStates[index] = updatedCountryStates[index].copy(isLoading = false, data = data)
                } catch (e: Exception) {
                    updatedCountryStates[index] = updatedCountryStates[index].copy(isLoading = false, error = e.localizedMessage)
                }
                _uiState.value = MainScreenUiState.Success(updatedCountryStates.toList())
            }
        }
    }

    /**
     * Obtiene los datos de COVID para un país específico.
     *
     * @param countryName El nombre del país a consultar.
     * @return [CovidDataResponse] si la obtención es exitosa, `null` si ocurre un error.
     */
    suspend fun getCovidDataForCountry(countryName: String): CovidDataResponse? {
        return try {
            getDataUseCase(country = countryName).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

}