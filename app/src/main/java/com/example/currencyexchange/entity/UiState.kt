package com.example.currencyexchange.entity

data class UiState(
    val senderCurrencyCode : SenderCurrencyCode = SenderCurrencyCode.GBP,
    val senderCurrencyValue : String = "",
    val receiverCurrencyValue : String = "",
    val currencyRate: String = "",
    val effectiveDate: String = "",
    val error: UiError? = null
){
    enum class SenderCurrencyCode {
        UAH, GBP, INR

    }

    enum class UiError {
        NoInternet, NoValues, TwoValues, WrongSendValue, WrongReceiveValue
    }
}

