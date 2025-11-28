package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.presentation.viewmodel.ComparisonCountryData
import com.covid.tqp.presentation.viewmodel.ComparisonViewModel

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
    val comparisonData = uiState.comparisonData

    Scaffold(
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
        // LazyRow permite el desplazamiento horizontal si hay muchos países.
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(comparisonData.values.toList()) { countryData ->
                ComparisonCard(data = countryData)
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
        modifier = Modifier.width(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
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
            } else if (data.data != null) {
                // Para simplificar, mostramos el total del último día disponible.
                val latestCases = data.data.cases?.values?.firstOrNull()?.total ?: 0
                val latestDeaths = data.data.deaths?.values?.firstOrNull()?.total ?: 0

                ComparisonStat(label = "Total Casos", value = latestCases.toString())
                Spacer(Modifier.height(12.dp))
                ComparisonStat(label = "Total Muertes", value = latestDeaths.toString())
            } else {
                Text("Datos no disponibles", textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * Composable simple para mostrar una etiqueta y un valor.
 */
@Composable
private fun ComparisonStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}