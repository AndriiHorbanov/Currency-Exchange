package com.example.currencyexchange.entity

data class RateInfo(
    val code: String = "",
    val currency: String = "",
    val rates: List<ExchangeRate> = listOf(ExchangeRate()),
    val table: String = ""
)