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
 * Actividad principal de la aplicación.
 *
 * Esta actividad es el punto de entrada de la interfaz de usuario y aloja el [NavHost] de Jetpack Compose
 * que gestiona la navegación entre las diferentes pantallas de la aplicación.
 * Está anotada con [AndroidEntryPoint] para habilitar la inyección de dependencias de Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Se llama cuando se crea la actividad.
     *
     * Configura la vista de la aplicación utilizando Jetpack Compose, estableciendo el tema,
     * una superficie de fondo y el [CovidAppNavHost] que controla la navegación.
     *
     * @param savedInstanceState Si la actividad se reinicia después de haber sido cerrada,
     * este [Bundle] contiene los datos que suministró más recientemente en [onSaveInstanceState].
     * En otro caso es nulo.
     */
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

/**
 * Composable que define el grafo de navegación de la aplicación utilizando [NavHost].
 *
 * Configura todas las rutas de navegación, asociando cada destino (una cadena de [AppDestinations])
 * con su respectivo Composable de pantalla.
 *
 * @see AppDestinations
 */
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
            CountryDetailScreen(navController = navController)
        }
    }
}