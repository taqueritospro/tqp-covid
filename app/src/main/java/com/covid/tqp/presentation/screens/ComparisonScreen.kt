package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.presentation.viewmodel.ComparisonCountryData
import com.covid.tqp.presentation.viewmodel.ComparisonViewModel
import com.covid.tqp.presentation.viewmodel.DailyDataSummary

/**
 * Pantalla que muestra una comparación lado a lado de las estadísticas de COVID
 * para los países seleccionados.
 *
 * @param navController Controlador de navegación.
 * @param viewModel El [ComparisonViewModel] que gestiona el estado de esta pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    navController: NavController,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Comparación de Países") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Solo muestra el selector de fecha si hay fechas disponibles.
            if (uiState.availableDates.isNotEmpty()) {
                DateSelector(
                    selectedDate = uiState.selectedDate,
                    availableDates = uiState.availableDates,
                    onDateSelected = viewModel::onDateSelected,
                    onInvalidDateSelected = {
                        // Aquí podrías mostrar un Snackbar si el usuario selecciona una fecha sin datos.
                    }
                )
            }

            // LazyRow permite el desplazamiento horizontal si hay muchos países.
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.comparisonData.values.toList()) { countryData ->
                    ComparisonCard(data = countryData)
                }
            }
        }
    }
}

/**
 * Tarjeta que muestra las estadísticas de un solo país en la pantalla de comparación.
 *
 * @param data Los datos de comparación para un país.
 */
@Composable
private fun ComparisonCard(data: ComparisonCountryData) {
    Card(
        modifier = Modifier
            .width(250.dp) // Ancho aumentado para más datos
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.countryName,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            if (data.isLoading) {
                CircularProgressIndicator()
            } else if (data.dataForSelectedDate != null) {
                // Muestra las estadísticas para la fecha seleccionada.
                val stats = data.dataForSelectedDate
                ComparisonStat(label = "Total Casos", value = stats.totalCases.toString())
                ComparisonStat(label = "Nuevos Casos", value = stats.newCases.toString())
                Spacer(Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                Spacer(Modifier.height(12.dp))
                ComparisonStat(label = "Total Muertes", value = stats.totalDeaths.toString())
                ComparisonStat(label = "Nuevas Muertes", value = stats.newDeaths.toString())
            } else {
                Text("Sin datos para esta fecha", textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

/**
 * Composable simple para mostrar una etiqueta y un valor.
 */
@Composable
private fun ComparisonStat(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}