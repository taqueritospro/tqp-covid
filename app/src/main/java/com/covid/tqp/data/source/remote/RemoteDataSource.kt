package com.covid.tqp.data.source.remote

import com.covid.tqp.data.model.CovidDataResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface Covid19ApiService {
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

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: Covid19ApiService
) {

    suspend fun getCovidData(
        date: String? = null,
        country: String? = null,
        region: String? = null,
        county: String? = null,
        type: String? = null
    ): List<CovidDataResponse> {
        return apiService.getCovidData(
            apiKey = "",
            date = date,
            country = country,
            region = region,
            county = county,
            type = type
        )
    }
}