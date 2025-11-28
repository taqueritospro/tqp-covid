package com.covid.tqp.domain.usecase

import com.covid.tqp.data.model.CovidDataResponse
import com.covid.tqp.domain.repository.DataRepository
import javax.inject.Inject

class GetDataUseCase @Inject constructor(
    private val dataRepository: DataRepository
) {
    suspend operator fun invoke(country: String? = null, date: String? = null): List<CovidDataResponse> {
        return dataRepository.getCovidData(country, date)
    }
}