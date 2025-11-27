package com.covid.tqp.data.source.remote

import com.covid.tqp.data.model.CovidDataResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

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
const val API_KEY = "NwZTeoCbXfI1odM2m7c9Tw==iGrJdu4eP7xY6eQv"

class RemoteDataSource {

    private val apiService: Covid19ApiService

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(Covid19ApiService::class.java)
    }

    suspend fun getCovidData(
        date: String? = null,
        country: String? = null,
        region: String? = null,
        county: String? = null,
        type: String? = null
    ): List<CovidDataResponse> {
        return apiService.getCovidData(API_KEY, date, country, region, county, type)
    }

    // Interceptor para añadir automáticamente la API Key a los headers
    private class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("X-Api-Key", API_KEY)
                .build()
            return chain.proceed(newRequest)
        }
    }

    // El método `getData()` original ha sido actualizado para usar el nuevo `getCovidData`
    // Considera renombrarlo o eliminarlo si ya no es necesario.
    suspend fun getData(): List<CovidDataResponse> {
        // Ejemplo de uso: Obtener datos para un país específico
        return getCovidData(country = "Canada")
    }
}