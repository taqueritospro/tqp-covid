package com.covid.tqp.domain.usecase

import com.covid.tqp.domain.model.DomainModel
import com.covid.tqp.domain.repository.DataRepository

class GetDataUseCase(
    private val dataRepository: DataRepository
) {
    suspend operator fun invoke(): List<DomainModel> {
        return dataRepository.getData()
    }
}