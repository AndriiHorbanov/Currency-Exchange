    package com.example.currencyexchange

    import android.app.Application
    import android.util.Log
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.currencyexchange.ScreenEvent.*
    import com.example.currencyexchange.entity.UiState
    import com.example.currencyexchange.network.ExchangeApi
    import com.example.currencyexchange.network.InternetCheckerWrapper
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch

    class ExchangeViewModel(
        application: Application,
        private val internetCheckerWrapper: InternetCheckerWrapper
    ) : AndroidViewModel(application) {

        private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState

        private var lastRate: Double = 0.0
        private var lastDate: String = ""

        private val currentUiState
            get() = uiState.value

        fun handleEvents(event: ScreenEvent) {
            when (event) {
                is ChangeSenderCurrencyValue -> changeSenderCurrencyValue(event)
                is ChangeReceiverCurrencyValue -> changeReceiverCurrencyValue(event)
                is ChangeSenderCurrencyCode -> changeSenderCurrencyCode(event)
                Charge -> tryToConvert()
                Clear -> clear()
                HideError -> hideError()
            }
        }

        private fun changeSenderCurrencyCode(event: ChangeSenderCurrencyCode) {
            _uiState.update { currentUiState.copy(senderCurrencyCode = event.code) }
        }


        private fun changeSenderCurrencyValue(event: ChangeSenderCurrencyValue) {
            _uiState.update { currentUiState.copy(senderCurrencyValue = event.value) }
        }

        private fun changeReceiverCurrencyValue(event: ChangeReceiverCurrencyValue) {
            _uiState.update { currentUiState.copy(receiverCurrencyValue = event.value) }
        }

        private fun clear() {
            _uiState.update {
                currentUiState.copy(
                    senderCurrencyValue = "",
                    receiverCurrencyValue = ""
                )
            }
        }

        private fun tryToConvert() {
            with(currentUiState) {
                when {

                    internetCheckerWrapper.isConnected(getApplication()).not() -> {
                        _uiState.update { copy(error = UiState.UiError.NoInternet) }
                    }

                    senderCurrencyValue.isBlank() && receiverCurrencyValue.isBlank() -> {
                        _uiState.update { copy(error = UiState.UiError.NoValues) }
                    }
                    senderCurrencyValue.isNotBlank() && receiverCurrencyValue.isNotBlank() -> {
                        _uiState.update { copy(error = UiState.UiError.TwoValues) }
                    }
                    senderCurrencyValue.isNotBlank() -> {
                        if (senderCurrencyValue.toDoubleOrNull() == null || senderCurrencyValue.toDouble() <= 0) {
                            _uiState.update { copy(error = UiState.UiError.WrongSendValue) }
                        } else {
                            viewModelScope.launch {
                                getData()
                                convertSendToReceive()
                            }
                            Unit
                        }
                    }

                    receiverCurrencyValue.isNotBlank() -> {
                        if (receiverCurrencyValue.toDoubleOrNull() == null || receiverCurrencyValue.toDouble() <= 0) {
                            _uiState.update { copy(error = UiState.UiError.WrongReceiveValue) }
                        } else {
                            viewModelScope.launch {
                                getData()
                                convertReceiveToSend()
                            }
                            Unit
                        }
                    }

                }
            }
        }


        private fun hideError() {
            _uiState.update { currentUiState.copy(error = null) }
        }


        private fun convertReceiveToSend() {
            val result = currentUiState.receiverCurrencyValue.toDouble() / lastRate
            _uiState.update {
                currentUiState.copy(
                    senderCurrencyValue = String.format("%.2f", result)
                )
            }
        }

        private fun convertSendToReceive() {
            val result = currentUiState.senderCurrencyValue.toDouble() * lastRate
            _uiState.update {
                currentUiState.copy(
                    receiverCurrencyValue = String.format("%.2f", result)
                )
            }
        }

        private fun updateRateAndDate() {
            _uiState.update {
                currentUiState.copy(
                    currencyRate = String.format("%.2f", lastRate),
                    effectiveDate = lastDate
                )
            }
        }


        private suspend fun getData() {
            try {
                val response =
                    ExchangeApi.retrofitService.getExchangeRate(currentUiState.senderCurrencyCode.name)
                with(response.rates[0]) {
                    lastRate = mid
                    lastDate = effectiveDate
                }
                updateRateAndDate()

            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to get exchange rate", e)

            }
        }

    }