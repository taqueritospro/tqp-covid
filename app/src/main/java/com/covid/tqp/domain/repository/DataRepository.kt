package com.covid.tqp.domain.repository

import com.covid.tqp.data.model.CovidDataResponse

/**
 * Interfaz del repositorio de datos que define las operaciones disponibles para obtener información de COVID-19.
 * Esta interfaz es parte de la capa de dominio en la arquitectura limpia y es implementada por [com.covid.tqp.data.repository.DataRepositoryImpl].
 */
interface DataRepository {
    /**
     * Obtiene datos de COVID-19 para un país y una fecha/tipo específicos.
     *
     * @param country El nombre del país a buscar. Si es nulo, la API podría devolver datos globales o un error.
     * @param date La fecha específica (formato YYYY-MM-DD) para la cual se desean los datos. Si es nulo, se obtendrán todos los datos históricos disponibles para el [country].
     * @param type El tipo de datos a recuperar: "cases" o "deaths". Si es nulo, la API podría usar un valor por defecto o requerir uno.
     * @return Una lista de [CovidDataResponse] que contiene los datos de COVID-19. Puede ser vacía si no se encuentran datos.
     */
    suspend fun getCovidData(country: String? = null, date: String? = null, type: String? = null): List<CovidDataResponse>
}