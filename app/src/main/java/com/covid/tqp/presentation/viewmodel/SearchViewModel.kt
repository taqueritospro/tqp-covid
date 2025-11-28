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

/**
 * Define los posibles estados de la UI para la pantalla de búsqueda ([SearchScreen]).
 */
sealed class SearchUiState {
    /**
     * El estado inicial o inactivo, antes de que se realice una búsqueda.
     */
    object Idle : SearchUiState()

    /**
     * Indica que una búsqueda está en curso.
     */
    object Loading : SearchUiState()

    /**
     * Indica que la búsqueda se completó con éxito.
     * @property countryName El nombre del país buscado.
     * @property data Los datos de COVID encontrados para el país. Puede ser nulo si no se encontraron datos.
     */
    data class Success(val countryName: String, val data: CovidDataResponse?) : SearchUiState()

    /**
     * Indica que ocurrió un error durante la búsqueda.
     * @property message El mensaje de error a mostrar.
     */
    data class Error(val message: String) : SearchUiState()
}

/**
 * [ViewModel] para la pantalla de búsqueda ([SearchScreen]).
 *
 * Gestiona la lógica de búsqueda de países, el estado de la UI y la interacción con las preferencias del usuario.
 *
 * @param getDataUseCase El caso de uso para obtener los datos de COVID.
 * @param userPreferencesRepository El repositorio para guardar y recuperar preferencias del usuario.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    /**
     * El flujo de estado para la consulta de búsqueda actual introducida por el usuario.
     */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    /**
     * El flujo de estado de la UI para la pantalla de búsqueda, observado por la vista.
     */
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    init {
        loadLastViewedCountry()
    }

    /**
     * Carga el último país consultado desde [UserPreferencesRepository] y lo establece como
     * la consulta de búsqueda inicial.
     */
    private fun loadLastViewedCountry() {
        viewModelScope.launch {
            userPreferencesRepository.lastViewedCountry.collect { country ->
                country?.let { _searchQuery.value = it }
            }
        }
    }

    /**
     * Se llama cuando el texto de búsqueda cambia en la UI.
     * @param query La nueva consulta de búsqueda.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Ejecuta la búsqueda de un país utilizando el valor actual de [searchQuery].
     *
     * Valida que la consulta no esté vacía, actualiza el [searchUiState] a [SearchUiState.Loading],
     * y luego intenta obtener los datos a través de [getDataUseCase].
     * - Si tiene éxito, guarda el país en las preferencias y actualiza el estado a [SearchUiState.Success].
     * - Si no se encuentran datos, actualiza el estado a [SearchUiState.Error].
     * - Si ocurre una excepción, actualiza el estado a [SearchUiState.Error] con el mensaje de error.
     */
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

    /**
     * Restablece el estado de la búsqueda a [SearchUiState.Idle].
     */
    fun resetSearchState() {
        _searchUiState.value = SearchUiState.Idle
    }
}