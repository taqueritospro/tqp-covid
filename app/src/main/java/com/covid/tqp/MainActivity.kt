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
import com.covid.tqp.presentation.theme.ExamenArgumentativoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de la aplicación que aloja la navegación de Jetpack Compose.
 * Anotada con [AndroidEntryPoint] para permitir la inyección de dependencias con Hilt.
 */
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
                    // Configura el host de navegación para la aplicación.
                    CovidAppNavHost()
                }
            }
        }
    }
}

/**
 * Define el [NavHost] para la aplicación, gestionando las diferentes pantallas y sus rutas.
 */
@Composable
fun CovidAppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.SPLASH_ROUTE) {
        // Ruta para la pantalla de inicio (Splash Screen)
        composable(AppDestinations.SPLASH_ROUTE) {
            SplashScreen(navController = navController)
        }
        // Ruta para la pantalla principal con la lista de países
        composable(AppDestinations.MAIN_ROUTE) {
            MainScreen(navController = navController)
        }
        // Ruta para la pantalla de búsqueda de países
        composable(AppDestinations.SEARCH_ROUTE) {
            SearchScreen(navController = navController)
        }
        // Ruta para la pantalla de detalle de un país específico
        // El argumento 'countryName' se extrae de la URL para pasarlo al ViewModel.
        composable(AppDestinations.COUNTRY_DETAIL_FULL_ROUTE) {
            CountryDetailScreen(navController = navController)
        }
    }
}