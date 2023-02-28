package com.example.currencyexchange

import android.app.Application
import com.example.currencyexchange.ScreenEvent.*
import com.example.currencyexchange.entity.UiState
import com.example.currencyexchange.network.InternetCheckerWrapper
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(ExchangeViewModel::class, InternetCheckerWrapper::class)
class ViewModelTest {

    private lateinit var viewModel: ExchangeViewModel

    @Mock
    private lateinit var internetCheckerWrapper: InternetCheckerWrapper

    @Mock
    private lateinit var mockContext: Application


    @Before
    fun setUpViewModel() {
        mockStatic(InternetCheckerWrapper::class.java)
        PowerMockito.`when`(internetCheckerWrapper.isConnected(mockContext)).thenReturn(true)
        viewModel = ExchangeViewModel(mockContext, internetCheckerWrapper)
    }

    fun setInternet(internet: Boolean) {
        mockStatic(InternetCheckerWrapper::class.java)
        PowerMockito.`when`(internetCheckerWrapper.isConnected(mockContext)).thenReturn(internet)
    }

    @Test
    fun `ChangeSenderCurrencyCode updates senderCurrencyCode in state`() {



        viewModel.handleEvents(ChangeSenderCurrencyCode(UiState.SenderCurrencyCode.INR))
        assertEquals(UiState.SenderCurrencyCode.INR, viewModel.uiState.value.senderCurrencyCode)
    }

    @Test
    fun `ChangeSenderCurrencyValue updates senderCurrencyValue in state`() {



        viewModel.handleEvents(ChangeSenderCurrencyValue("1"))
        assertEquals("1", viewModel.uiState.value.senderCurrencyValue)
    }

    @Test
    fun `ChangeReceiverCurrencyValue updates senderCurrencyValue in state`() {
        viewModel.handleEvents(ChangeReceiverCurrencyValue("1"))
        assertEquals("1", viewModel.uiState.value.receiverCurrencyValue)
    }


    @Test
    fun `Clean updates senderCurrencyValue and receiverCurrencyValue in state`() {



        viewModel.handleEvents(ChangeSenderCurrencyValue("1"))
        viewModel.handleEvents(ChangeReceiverCurrencyValue("1"))
        viewModel.handleEvents(Clear)

        assertEquals("", viewModel.uiState.value.senderCurrencyValue)
        assertEquals("", viewModel.uiState.value.receiverCurrencyValue)
    }


    @Test
    fun `Charge update error in state NoValues`() {


        viewModel.handleEvents(ChangeSenderCurrencyValue(""))
        viewModel.handleEvents(ChangeReceiverCurrencyValue(""))
        viewModel.handleEvents(Charge)

        assertEquals(UiState.UiError.NoValues, viewModel.uiState.value.error)

    }

    @Test
    fun `Charge update error in state WrongSendValue`() {


        viewModel.handleEvents(ChangeSenderCurrencyValue("-1"))
        viewModel.handleEvents(Charge)
        assertEquals(UiState.UiError.WrongSendValue, viewModel.uiState.value.error)
    }


    @Test
    fun `Charge update error in state WrongReceiveValue`() {

        viewModel.handleEvents(ChangeReceiverCurrencyValue("-1"))
        viewModel.handleEvents(Charge)
        assertEquals(UiState.UiError.WrongReceiveValue, viewModel.uiState.value.error)
    }

    @Test
    fun `Charge update error in state NoInternet`() {
        setInternet(false)


        viewModel.handleEvents(ChangeSenderCurrencyValue("1"))
        viewModel.handleEvents(ChangeReceiverCurrencyValue("1"))
        viewModel.handleEvents(Charge)
        assertEquals(UiState.UiError.NoInternet, viewModel.uiState.value.error)
    }

    @Test
    fun `HideError update error in state error = null`() {
        setInternet(false)


        viewModel.handleEvents(ChangeSenderCurrencyValue("1"))
        viewModel.handleEvents(ChangeReceiverCurrencyValue("1"))
        viewModel.handleEvents(HideError)
        assertEquals(null, viewModel.uiState.value.error)
    }


}

