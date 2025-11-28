package com.covid.tqp.navigation

/**
 * Contiene todas las constantes de rutas y argumentos de navegación de la aplicación.
 *
 * Este objeto centraliza las cadenas de ruta para evitar errores y facilitar el mantenimiento.
 * Proporciona una forma segura y consistente de navegar entre las diferentes pantallas.
 */
object AppDestinations {
    /**
     * Ruta de navegación para la pantalla de bienvenida ([SplashScreen]).
     */
    const val SPLASH_ROUTE = "splash_route"

    /**
     * Ruta de navegación para la pantalla principal ([MainScreen]) que muestra la lista de países.
     */
    const val MAIN_ROUTE = "main_route"

    /**
     * Ruta de navegación para la pantalla de búsqueda ([SearchScreen]).
     */
    const val SEARCH_ROUTE = "search_route"

    /**
     * Ruta base para la pantalla de detalle de un país ([CountryDetailScreen]).
     * Esta ruta no incluye los argumentos.
     */
    const val COUNTRY_DETAIL_ROUTE = "country_detail_route"

    /**
     * Nombre de la clave del argumento utilizado para pasar el nombre del país a [CountryDetailScreen].
     */
    const val COUNTRY_DETAIL_ARGUMENT = "countryName"

    /**
     * Ruta completa para la pantalla de detalle de un país, incluyendo el marcador de posición para el argumento [COUNTRY_DETAIL_ARGUMENT].
     * Ejemplo de uso: `navController.navigate("$COUNTRY_DETAIL_ROUTE/Mexico")`
     */
    const val COUNTRY_DETAIL_FULL_ROUTE = "$COUNTRY_DETAIL_ROUTE/{$COUNTRY_DETAIL_ARGUMENT}"
}