package com.example.application

import retrofit2.http.GET

interface CurrencyApiService {

    @GET("v4/latest/USD")
    suspend fun getExchangeRates(): ApiRateResponse
}