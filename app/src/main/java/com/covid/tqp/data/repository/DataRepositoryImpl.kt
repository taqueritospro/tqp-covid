package com.covid.tqp.data.repository

import com.covid.tqp.data.source.remote.RemoteDataSource
import com.covid.tqp.domain.model.DomainModel
import com.covid.tqp.domain.repository.DataRepository

class DataRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : DataRepository {
    override suspend fun getData(): List<DomainModel> {
        return remoteDataSource.getData().map { dataModel ->
            DomainModel(dataModel.id, "name_from_data_model") // Simplified mapping
        }
    }
}