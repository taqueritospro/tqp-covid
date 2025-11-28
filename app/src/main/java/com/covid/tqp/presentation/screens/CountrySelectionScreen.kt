package com.covid.tqp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.navigation.AppDestinations
import com.covid.tqp.presentation.viewmodel.CountrySelectionViewModel

/**
 * Pantalla que permite al usuario seleccionar múltiples países para comparar sus estadísticas.
 *
 * @param navController Controlador de navegación.
 * @param viewModel El [CountrySelectionViewModel] que gestiona el estado de esta pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectionScreen(
    navController: NavController,
    viewModel: CountrySelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCountries = uiState.selectedCountries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Países") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            // El FAB solo se muestra si se han seleccionado 2 o más países.
            if (selectedCountries.size >= 2) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Navega a la pantalla de comparación, pasando los países como una cadena separada por comas.
                        val selectedCountriesStr = selectedCountries.joinToString(",")
                        navController.navigate("${AppDestinations.COMPARISON_ROUTE}/$selectedCountriesStr")
                    },
                    icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Comparar") },
                    text = { Text("Comparar (${selectedCountries.size})") }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Muestra un indicador de carga centrado.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Muestra la lista de países una vez cargada.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(uiState.countries) { countryName ->
                    CountrySelectItem(
                        countryName = countryName,
                        isSelected = selectedCountries.contains(countryName),
                        onToggle = { viewModel.toggleCountrySelection(countryName) }
                    )
                }
            }
        }
    }
}

/**
 * Un elemento de la lista para un país individual.
 *
 * @param countryName El nombre del país.
 * @param isSelected `true` si el país está seleccionado.
 * @param onToggle Callback que se invoca cuando se hace clic en el elemento.
 */
@Composable
private fun CountrySelectItem(
    countryName: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null // El clic se maneja en el Row.
        )
        Spacer(Modifier.width(16.dp))
        Text(text = countryName, style = MaterialTheme.typography.bodyLarge)
    }
}