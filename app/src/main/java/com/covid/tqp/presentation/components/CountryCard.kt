package com.covid.tqp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Un Composable que muestra una tarjeta para un país.
 *
 * Puede mostrar el nombre del país y un icono, o un efecto de "shimmer" (brillo)
 * mientras los datos se están cargando. Es clickeable para permitir la navegación.
 *
 * @param countryName El nombre del país a mostrar.
 * @param isLoading Un booleano que indica si se debe mostrar el estado de carga (shimmer).
 * @param onClick La acción a ejecutar cuando se hace clic en la tarjeta.
 */
@Composable
fun CountryCard(
    countryName: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                // Aplica el efecto shimmer si isLoading es verdadero.
                .run { if (isLoading) shimmerEffect() else this },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                // Espaciadores que simulan la estructura del contenido durante la carga.
                Spacer(modifier = Modifier.height(30.dp))
                Spacer(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .background(Color.LightGray)
                )
            } else {
                // Contenido real de la tarjeta.
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Icono de Mundo",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = countryName,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}