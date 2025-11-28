package com.covid.tqp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de respuesta completa de la API de Covid-19.
 * Contiene información general del país/región y los mapas de datos diarios para casos y muertes.
 *
 * @property country El nombre del país.
 * @property region El nombre de la región administrativa (puede ser nulo).
 * @property cases Un mapa donde la clave es la fecha (YYYY-MM-DD) y el valor son los datos diarios de casos.
 * @property deaths Un mapa donde la clave es la fecha (YYYY-MM-DD) y el valor son los datos diarios de muertes.
 */
data class CovidDataResponse(
    val country: String,
    val region: String?,
    val cases: Map<String, DailyData>?,
    val deaths: Map<String, DailyData>?
)

/**
 * Representa los datos diarios de COVID-19 (casos o muertes) para una fecha específica.
 *
 * @property total El número total acumulado hasta esa fecha.
 * @property new El número de casos o muertes nuevos reportados en esa fecha.
 */
data class DailyData(
    val total: Int,
    val new: Int
)
