package com.covid.tqp.data.model

import com.google.gson.annotations.SerializedName

data class CovidDataResponse(
    val country: String,
    val region: String?,
    val cases: Map<String, DailyData>?,
    val deaths: Map<String, DailyData>?
)

data class DailyData(
    val total: Int,
    val new: Int
)
