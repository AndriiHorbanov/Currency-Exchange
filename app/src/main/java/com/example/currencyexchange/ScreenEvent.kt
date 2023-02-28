package com.example.currencyexchange

import com.example.currencyexchange.entity.UiState

sealed interface ScreenEvent{
    object Charge: ScreenEvent
    object Clear: ScreenEvent
    data class ChangeSenderCurrencyCode(val code: UiState.SenderCurrencyCode): ScreenEvent
    data class ChangeSenderCurrencyValue(val value: String): ScreenEvent
    data class ChangeReceiverCurrencyValue(val value: String): ScreenEvent
    object HideError: ScreenEvent
}