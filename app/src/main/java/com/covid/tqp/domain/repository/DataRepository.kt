package com.covid.tqp.domain.repository

import com.covid.tqp.domain.model.DomainModel

interface DataRepository {
    suspend fun getData(): List<DomainModel>
}