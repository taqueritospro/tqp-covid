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

@Composable
fun CountryCard(
    countryName: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Altura reducida
            .clickable(enabled = !isLoading, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .run { if (isLoading) shimmerEffect() else this },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Spacer(modifier = Modifier.height(30.dp))
                Spacer(modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .background(Color.LightGray)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Flag of $countryName",
                    modifier = Modifier.size(40.dp), // Reducir un poco el tamaño del icono
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp)) // Reducir el espacio
                Text(
                    text = countryName,
                    style = MaterialTheme.typography.titleSmall // Usar estilo más pequeño
                )
            }
        }
    }
}