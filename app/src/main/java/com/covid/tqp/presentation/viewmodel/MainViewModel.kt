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

data class CountryUiState(
    val countryName: String,
    val isLoading: Boolean = false,
    val data: CovidDataResponse? = null,
    val error: String? = null
)

sealed class MainScreenUiState {
    object Loading : MainScreenUiState()
    data class Success(val countries: List<CountryUiState>) : MainScreenUiState()
    data class Error(val message: String) : MainScreenUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    val predefinedCountries = listOf(
        "Mexico", "Canada", "Honduras", "China", "Japan",
        "South Korea", "Finland", "Brazil", "France", "Argentina"
    )

    init {
        fetchInitialCountryData()
    }

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

    suspend fun getCovidDataForCountry(countryName: String): CovidDataResponse? {
        return try {
            getDataUseCase(country = countryName).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

}