package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.presentation.viewmodel.CountryDetailViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

/**
 * Composable que representa la pantalla de detalles de un país específico.
 *
 * Muestra las estadísticas de COVID (casos totales, nuevos casos, muertes totales, nuevas muertes)
 * para una fecha seleccionada. Permite al usuario cambiar la fecha utilizando un [DatePicker].
 * El estado es proporcionado por [CountryDetailViewModel].
 *
 * @param navController Controlador de navegación para acciones como volver a la pantalla anterior.
 * @param viewModel Instancia de [CountryDetailViewModel] inyectada por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    navController: NavController,
    viewModel: CountryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.countryName) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                    Text("Cargando datos...", modifier = Modifier.padding(top = 16.dp))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    // Componente para seleccionar la fecha.
                    DateSelector(
                        selectedDate = uiState.selectedDate,
                        availableDates = uiState.availableDates,
                        onDateSelected = viewModel::onDateSelected,
                        onInvalidDateSelected = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No hay datos disponibles para la fecha seleccionada.")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Muestra las tarjetas de datos o un mensaje si no hay datos.
                    uiState.dataForSelectedDate?.let { data ->
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { DataCard(icon = Icons.Default.Coronavirus, label = "Total Casos", value = data.totalCases.toString()) }
                            item { DataCard(icon = Icons.Default.Cases, label = "Nuevos Casos", value = data.newCases.toString()) }
                            item { DataCard(icon = Icons.Default.LocalHospital, label = "Total Muertes", value = data.totalDeaths.toString()) }
                            item { DataCard(icon = Icons.Default.DateRange, label = "Nuevas Muertes", value = data.newDeaths.toString()) }
                        }
                    } ?: run {
                        Text(
                            text = "No hay datos para: ${uiState.selectedDate.format(DateTimeFormatter.ISO_DATE)}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Un Composable reutilizable para mostrar una pieza de información estadística.
 *
 * @param icon El [ImageVector] que se mostrará junto a la etiqueta.
 * @param label El texto descriptivo para el dato.
 * @param value El valor numérico del dato, como [String].
 */
@Composable
fun DataCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

/**
 * Un Composable que encapsula la lógica para mostrar un botón que abre un [DatePickerDialog].
 *
 * @param selectedDate La fecha actualmente seleccionada.
 * @param availableDates Un conjunto de fechas válidas que el usuario puede seleccionar.
 * @param onDateSelected Callback que se invoca cuando el usuario confirma una fecha válida.
 * @param onInvalidDateSelected Callback que se invoca si el usuario selecciona una fecha sin datos disponibles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedDate: LocalDate,
    availableDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onInvalidDateSelected: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDatePicker = true }) {
        Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar Fecha")
        Spacer(Modifier.width(8.dp))
        Text(selectedDate.format(DateTimeFormatter.ISO_DATE))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (availableDates.contains(newDate)) {
                            onDateSelected(newDate)
                        } else {
                            onInvalidDateSelected()
                        }
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}