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

@OptIn(ExperimentalMaterial3Api::class)
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
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

            when (searchUiState) {
                is SearchUiState.Idle -> {
                    Text("Introduce un país para buscar.", style = MaterialTheme.typography.bodyMedium)
                }
                is SearchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text("Buscando país...")
                }
                is SearchUiState.Success -> {
                    val countryName = (searchUiState as SearchUiState.Success).countryName
                    Text(
                        text = "País " + countryName + " encontrado con éxito!",
                        color = Color.Green.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(onClick = {
                        navController.navigate(
                            "${AppDestinations.COUNTRY_DETAIL_ROUTE}/${countryName}"
                        )
                        viewModel.resetSearchState() // Reiniciar estado después de navegar
                    }) {
                        Text("Ver Detalles")
                    }
                }
                is SearchUiState.Error -> {
                    val errorMessage = (searchUiState as SearchUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red.copy(alpha = 0.2f))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.searchCountry() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}