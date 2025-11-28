package com.covid.tqp.data.source.remote

import com.covid.tqp.data.model.CovidDataResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interfaz de Retrofit para consumir la API de Covid-19.
 * Define los endpoints y los parámetros para las solicitudes HTTP.
 */
interface Covid19ApiService {
    /**
     * Obtiene datos de COVID-19 de la API.
     *
     * @param apiKey La clave de la API, se inyecta automáticamente vía interceptor de OkHttp.
     * @param date Fecha para obtener un snapshot de un solo día (formato YYYY-MM-DD).
     * @param country Nombre del país (no sensible a mayúsculas y minúsculas).
     * @param region Nombre de la región administrativa (requiere [country]).
     * @param county Nombre del condado para estados de EE. UU. (requiere [country] y [region]).
     * @param type Tipo de datos a recuperar: "cases" o "deaths". Por defecto, "cases".
     * @return Una lista de [CovidDataResponse] que contiene los datos solicitados.
     */
    @GET("v1/covid19")
    suspend fun getCovidData(
        @Header("X-Api-Key") apiKey: String,
        @Query("date") date: String? = null,
        @Query("country") country: String? = null,
        @Query("region") region: String? = null,
        @Query("county") county: String? = null,
        @Query("type") type: String? = null
    ): List<CovidDataResponse>
}

/**
 * Fuente de datos remota para obtener información de COVID-19 de la API.
 * Esta clase actúa como un puente entre el repositorio y el servicio de la API [Covid19ApiService].
 * Inyecta [Covid19ApiService] a través del constructor, utilizando Hilt.
 *
 * @property apiService La instancia del servicio de la API de Covid-19.
 */
@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: Covid19ApiService
) {

    /**
     * Obtiene datos de COVID-19 de la API remota.
     * La clave de la API se gestiona mediante un interceptor de OkHttp, por lo que no se pasa explícitamente aquí.
     *
     * @param date Fecha para obtener un snapshot de un solo día (formato YYYY-MM-DD).
     * @param country Nombre del país.
     * @param region Nombre de la región administrativa.
     * @param county Nombre del condado para estados de EE. UU.
     * @param type Tipo de datos a recuperar: "cases" o "deaths".
     * @return Una lista de [CovidDataResponse] con los datos de COVID-19.
     */
    suspend fun getCovidData(
        date: String? = null,
        country: String? = null,
        region: String? = null,
        county: String? = null,
        type: String? = null
    ): List<CovidDataResponse> {
        // La API Key se añade vía interceptor de OkHttp, este valor no se usa directamente aquí.
        // Se pasa una cadena vacía o cualquier valor, ya que el interceptor lo reemplazará.
        return apiService.getCovidData(
            apiKey = "", // La API Key se añade via interceptor, este valor no se usa
            date = date,
            country = country,
            region = region,
            county = county,
            type = type
        )
    }
}