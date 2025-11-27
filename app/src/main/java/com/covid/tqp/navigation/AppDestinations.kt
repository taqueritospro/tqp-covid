package com.covid.tqp.navigation

object AppDestinations {
    const val SPLASH_ROUTE = "splash_route"
    const val MAIN_ROUTE = "main_route"
    const val SEARCH_ROUTE = "search_route"
    const val COUNTRY_DETAIL_ROUTE = "country_detail_route"

    const val COUNTRY_DETAIL_ARGUMENT = "countryName"
    const val COUNTRY_DETAIL_FULL_ROUTE = "$COUNTRY_DETAIL_ROUTE/{$COUNTRY_DETAIL_ARGUMENT}"
}