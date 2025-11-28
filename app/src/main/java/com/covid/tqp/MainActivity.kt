package com.covid.tqp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.covid.tqp.navigation.AppDestinations
import com.covid.tqp.presentation.screens.CountryDetailScreen
import com.covid.tqp.presentation.screens.MainScreen
import com.covid.tqp.presentation.screens.SearchScreen
import com.covid.tqp.presentation.screens.SplashScreen
import com.covid.tqp.ui.theme.ExamenArgumentativoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamenArgumentativoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CovidAppNavHost()
                }
            }
        }
    }
}

@Composable
fun CovidAppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.SPLASH_ROUTE) {
        composable(AppDestinations.SPLASH_ROUTE) {
            SplashScreen(navController = navController)
        }
        composable(AppDestinations.MAIN_ROUTE) {
            MainScreen(navController = navController)
        }
        composable(AppDestinations.SEARCH_ROUTE) {
            SearchScreen(navController = navController)
        }
        composable(AppDestinations.COUNTRY_DETAIL_FULL_ROUTE) {
            // Eliminamos el placeholder y usamos CountryDetailScreen
            CountryDetailScreen(navController = navController)
        }
    }
}