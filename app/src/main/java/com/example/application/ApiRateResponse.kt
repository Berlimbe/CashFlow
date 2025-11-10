package com.example.application

import com.google.gson.annotations.SerializedName

data class ApiRateResponse(
    @SerializedName("result") val result: String,
    @SerializedName("time_last_update_unix") val timeLastUpdateUnix: Long,
    @SerializedName("rates") val rates: Map<String, Double>
)

data class CurrencyRate(
    val moedaBase: String,
    val taxaBRL: Double
)