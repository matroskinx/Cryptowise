package com.kvladislav.cryptowise.screens.transaction

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class AddViewModel(private val context: Context) : BaseViewModel(), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()


    fun createAndAddTransaction() {
        Timber.d("Attempting to add transaction")
        viewModelScope.launch(Dispatchers.IO) {
            val customTransaction = BuySellTransaction(
                0,
                System.currentTimeMillis(),
                6000.0,
                0.005,
                0.0,
                100,
                "BTC",
                "My note"
            )
            transactionRepository.addTransaction(customTransaction)
        }
    }
}