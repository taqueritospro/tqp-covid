package com.covid.tqp.domain.repository

import com.covid.tqp.data.model.CovidDataResponse

interface DataRepository {
    suspend fun getCovidData(country: String? = null, date: String? = null): List<CovidDataResponse>
}