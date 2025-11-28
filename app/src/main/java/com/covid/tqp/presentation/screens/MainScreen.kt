package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.navigation.AppDestinations
import com.covid.tqp.presentation.components.CountryCard
import com.covid.tqp.presentation.viewmodel.MainScreenUiState
import com.covid.tqp.presentation.viewmodel.MainViewModel

/**
 * Composable que representa la pantalla principal de la aplicación.
 *
 * Muestra una cuadrícula de países predefinidos. El estado de la UI (carga, éxito, error)
 * es gestionado por el [MainViewModel]. Incluye botones de acción flotante (FAB)
 * para buscar y comparar países.
 *
 * @param navController El controlador de navegación para manejar las acciones de navegación.
 * @param viewModel La instancia de [MainViewModel] inyectada por Hilt, que proporciona el estado y la lógica de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    // Recolecta el estado de la UI del ViewModel como un State de Compose.
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestinations.SEARCH_ROUTE) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar país")
                }
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestinations.COUNTRY_SELECTION_ROUTE) },
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = "Comparar países")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            // Renderiza la UI en función del estado actual (when-expression).
            when (val state = uiState) {
                is MainScreenUiState.Loading -> {
                    // Muestra una cuadrícula de tarjetas con efecto shimmer mientras carga.
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 150.dp), contentPadding = PaddingValues(8.dp)) {
                        items(viewModel.predefinedCountries.size) {
                            CountryCard(
                                countryName = "Cargando...",
                                isLoading = true,
                                onClick = { /* No-op */ }
                            )
                        }
                    }
                }
                is MainScreenUiState.Success -> {
                    // Muestra la lista de países cuando los datos se cargan correctamente.
                    Text(
                        text = "Selecciona un país para ver estadísticas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.countries) { countryUiState ->
                            CountryCard(
                                countryName = countryUiState.countryName,
                                isLoading = countryUiState.isLoading,
                                onClick = {
                                    // Navega a la pantalla de detalle del país al hacer clic.
                                    navController.navigate(
                                        "${AppDestinations.COUNTRY_DETAIL_ROUTE}/${countryUiState.countryName}"
                                    )
                                }
                            )
                        }
                    }
                }
                is MainScreenUiState.Error -> {
                    // Muestra un mensaje de error si la carga falla.
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}