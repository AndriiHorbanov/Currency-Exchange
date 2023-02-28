    package com.example.currencyexchange

    import android.app.Application
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.currencyexchange.network.InternetCheckerWrapper

    @Suppress("UNCHECKED_CAST")
    class ViewModelFactory(
        private val application: Application,
        private val internetCheckerWrapper: InternetCheckerWrapper
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExchangeViewModel::class.java)) {
                return ExchangeViewModel(application, internetCheckerWrapper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }