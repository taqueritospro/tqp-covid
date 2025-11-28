package com.covid.tqp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.covid.tqp.navigation.AppDestinations
import com.covid.tqp.presentation.viewmodel.SearchUiState
import com.covid.tqp.presentation.viewmodel.SearchViewModel

/**
 * Composable que representa la pantalla de búsqueda de países.
 *
 * Permite al usuario introducir el nombre de un país en un campo de texto y ejecutar una búsqueda.
 * Muestra diferentes estados de la UI (inactivo, cargando, éxito, error) basados en el [SearchUiState]
 * proporcionado por el [SearchViewModel].
 *
 * @param navController El controlador de navegación para manejar acciones como volver atrás o navegar a la pantalla de detalles.
 * @param viewModel La instancia de [SearchViewModel] inyectada por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchUiState by viewModel.searchUiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar País") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Campo de texto para la búsqueda.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("Nombre del País") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    viewModel.searchCountry()
                }),
                trailingIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        viewModel.searchCountry()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Renderiza la UI según el estado de la búsqueda.
            when (val state = searchUiState) {
                is SearchUiState.Idle -> {
                    Text("Introduce un país para buscar.", style = MaterialTheme.typography.bodyMedium)
                }
                is SearchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text("Buscando país...", modifier = Modifier.padding(top = 8.dp))
                }
                is SearchUiState.Success -> {
                    // Muestra un mensaje de éxito y un botón para ver los detalles.
                    Text(
                        text = "País \"${state.countryName}\" encontrado con éxito!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(onClick = {
                        navController.navigate("${AppDestinations.COUNTRY_DETAIL_ROUTE}/${state.countryName}")
                        viewModel.resetSearchState() // Reinicia el estado después de navegar.
                    }) {
                        Text("Ver Detalles")
                    }
                }
                is SearchUiState.Error -> {
                    // Muestra un mensaje de error y un botón para reintentar.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.searchCountry() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}