package com.example.currencyexchange.network

import android.app.Application

interface InternetCheckerWrapper {
    fun isConnected(application: Application): Boolean
}