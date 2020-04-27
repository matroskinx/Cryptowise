package com.kvladislav.cryptowise.screens.transaction

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TransactionListViewModel(private val context: Context) : BaseViewModel(), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()

    val allTransactions: LiveData<List<BuySellTransaction>>

    init {
        allTransactions = transactionRepository.allTransactions
    }

    fun addTransaction(transaction: BuySellTransaction) =
        viewModelScope.launch(Dispatchers.IO) { transactionRepository.addTransaction(transaction) }
}