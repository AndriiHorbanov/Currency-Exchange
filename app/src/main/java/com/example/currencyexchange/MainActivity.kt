package com.example.currencyexchange

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.currencyexchange.ScreenEvent.*
import com.example.currencyexchange.databinding.ActivityMainBinding
import com.example.currencyexchange.databinding.WarningDialogBinding
import com.example.currencyexchange.entity.UiState.*
import com.example.currencyexchange.entity.UiState.SenderCurrencyCode.*
import com.example.currencyexchange.network.InternetChecker
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ExchangeViewModel by viewModels {
        ViewModelFactory(application, InternetChecker())
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeState()

        setListeners()

        createPopupMenu()


    }


    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    with(binding) {
                        rate.text = uiState.currencyRate
                        effectiveDate.text = uiState.effectiveDate
                        send.setTextWithSavingCursor(uiState.senderCurrencyValue)
                        receive.setTextWithSavingCursor(uiState.receiverCurrencyValue)
                        setSenderCurrencyCode(uiState.senderCurrencyCode)
                        uiState.error?.let { showWarningDialog(it) }
                    }
                }
            }
        }
    }

    private fun EditText.setTextWithSavingCursor(text: String) {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        setText(text)
        if (text.isNotEmpty()) setSelection(selectionStart, selectionEnd)

    }


    private fun EditText.setOnEditListener(listener: (String) -> Unit) {
        addTextChangedListener(onTextChanged = { text, _, _, _ ->
            listener(text?.toString() ?: "")
        })
    }

    private fun setListeners() {
        with(binding) {
            send.setOnEditListener{text ->
                viewModel.handleEvents(ChangeSenderCurrencyValue(text))
            }

            receive.setOnEditListener{text ->
                viewModel.handleEvents(ChangeReceiverCurrencyValue(text))
            }

            charge.setOnClickListener {
                viewModel.handleEvents(Charge)
            }

            clearText.setOnClickListener {
                viewModel.handleEvents(Clear)
            }

        }


    }


    private fun showWarningDialog(errorType: UiError) {
        val dialogBinding = WarningDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val errorMessage: CharSequence = dialogBinding.root.context.getString(
            when(errorType){
                UiError.NoInternet -> R.string.warning_no_internet
                UiError.NoValues -> R.string.warning_no_values
                UiError.WrongSendValue -> R.string.warning_wrong_send_value
                UiError.TwoValues -> R.string.warning_two_values
                UiError.WrongReceiveValue -> R.string.warning_wrong_receive_value
            }
        )
        dialogBinding.message.text = errorMessage
        dialogBinding.okButton.setOnClickListener {
            dialog.dismiss()
            viewModel.handleEvents(HideError)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun createPopupMenu() {
        binding.send.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.send.compoundDrawables[0]

                if (event.x <= drawable.bounds.width()) {
                    val popupMenu = PopupMenu(this, view, Gravity.START)

                    popupMenu.menu.add(UAH.name)
                    popupMenu.menu.add(GBP.name)
                    popupMenu.menu.add(INR.name)


                    //!event
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        menuItem.title?.let {
                            viewModel.handleEvents(
                                ChangeSenderCurrencyCode(
                                    SenderCurrencyCode.valueOf(it.toString())
                                )
                            )
                        }



                        true
                    }

                    popupMenu.show()

                    return@setOnTouchListener true
                }
            }

            false
        }
    }

    private fun setSenderCurrencyCode(code: SenderCurrencyCode) {
        with(binding.send) {
            hint = code.name
            setCompoundDrawablesWithIntrinsicBounds(
                when (code) {
                    UAH -> R.drawable.uah
                    INR -> R.drawable.india
                    GBP -> R.drawable.br
                },
                0,
                0,
                0,
            )
        }
    }


}
