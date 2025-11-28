package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(AppDestinations.SEARCH_ROUTE)
            }) {
                Icon(Icons.Default.Search, "Buscar país")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            when (uiState) {
                is MainScreenUiState.Loading -> {
                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                        items(viewModel.predefinedCountries.size) {
                            CountryCard(
                                countryName = "Loading",
                                isLoading = true,
                                onClick = { /* No clickeable mientras carga */ }
                            )
                        }
                    }
                }
                is MainScreenUiState.Success -> {
                    val countries = (uiState as MainScreenUiState.Success).countries
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(countries) { countryUiState ->
                            CountryCard(
                                countryName = countryUiState.countryName,
                                isLoading = countryUiState.isLoading,
                                onClick = {
                                    navController.navigate(
                                        "${AppDestinations.COUNTRY_DETAIL_ROUTE}/${countryUiState.countryName}"
                                    )
                                }
                            )
                        }
                    }
                }
                is MainScreenUiState.Error -> {
                    val errorMessage = (uiState as MainScreenUiState.Error).message
                    Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}