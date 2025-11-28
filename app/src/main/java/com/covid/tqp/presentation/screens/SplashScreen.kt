package com.covid.tqp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.covid.tqp.navigation.AppDestinations
import kotlinx.coroutines.delay

/**
 * Composable que representa la pantalla de bienvenida de la aplicación.
 *
 * Muestra un indicador de progreso y el nombre de la aplicación durante un breve período
 * y luego navega a la pantalla principal.
 *
 * @param navController El controlador de navegación utilizado para redirigir a la pantalla principal
 * después de que finalice la espera.
 */
@Composable
fun SplashScreen(
    navController: NavController
) {
    // Un LaunchedEffect se utiliza para ejecutar una corutina en el ámbito de este Composable.
    // `key1 = true` asegura que el efecto se ejecute solo una vez cuando el Composable entra en la composición.
    LaunchedEffect(key1 = true) {
        delay(2000) // Espera durante 2000 milisegundos (2 segundos).
        navController.popBackStack() // Elimina la pantalla de bienvenida del back stack para que el usuario no pueda volver a ella.
        navController.navigate(AppDestinations.MAIN_ROUTE) // Navega a la pantalla principal.
    }

    // Diseño de la pantalla centrado vertical y horizontalmente.
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Indicador de progreso circular.
        CircularProgressIndicator(modifier = Modifier.size(72.dp))
        // Texto con el título de la aplicación.
        Text(
            text = "Covid-19",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}