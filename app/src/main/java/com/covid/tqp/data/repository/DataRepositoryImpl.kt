package com.covid.tqp.data.repository

import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.data.source.remote.RemoteDataSource
import com.covid.tqp.domain.repository.DataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : DataRepository {
    override suspend fun getCovidData(country: String?, date: String?): List<CovidDataResponse> {
        return remoteDataSource.getCovidData(country = country, date = date)
    }
}