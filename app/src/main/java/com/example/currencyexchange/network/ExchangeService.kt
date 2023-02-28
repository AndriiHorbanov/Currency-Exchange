package com.example.currencyexchange.network

import com.example.currencyexchange.entity.RateInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://api.nbp.pl"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface ExchangeService {
    @GET("/api/exchangerates/rates/a/{currencyCode}/last/?format=json")
    suspend fun getExchangeRate(@Path("currencyCode") currencyCode: String): RateInfo
}

object ExchangeApi {
    val retrofitService: ExchangeService by lazy {
        retrofit.create(ExchangeService::class.java)
    }
}