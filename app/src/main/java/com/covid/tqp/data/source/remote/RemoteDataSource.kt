package com.covid.tqp.data.source.remote

import com.covid.tqp.data.model.DataModel

class RemoteDataSource {
    suspend fun getData(): List<DataModel> {
        // Simulate network request
        return listOf(DataModel("1"), DataModel("2"))
    }
}