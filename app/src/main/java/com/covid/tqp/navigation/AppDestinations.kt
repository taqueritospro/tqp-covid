package com.covid.tqp.navigation

/**
 * Objeto que define todas las rutas de navegación y argumentos para la aplicación.
 * Centraliza las cadenas de ruta para una navegación segura y consistente.
 */
object AppDestinations {
    /** Ruta para la pantalla de inicio (Splash Screen). */
    const val SPLASH_ROUTE = "splash_route"

    /** Ruta para la pantalla principal que muestra la lista de países. */
    const val MAIN_ROUTE = "main_route"

    /** Ruta para la pantalla de búsqueda de países. */
    const val SEARCH_ROUTE = "search_route"

    /** Ruta base para la pantalla de detalle de un país. */
    const val COUNTRY_DETAIL_ROUTE = "country_detail_route"

    /** Nombre del argumento utilizado para pasar el nombre del país a la pantalla de detalle. */
    const val COUNTRY_DETAIL_ARGUMENT = "countryName"

    /** Ruta completa para la pantalla de detalle de un país, incluyendo el argumento. */
    const val COUNTRY_DETAIL_FULL_ROUTE = "$COUNTRY_DETAIL_ROUTE/{$COUNTRY_DETAIL_ARGUMENT}"
}