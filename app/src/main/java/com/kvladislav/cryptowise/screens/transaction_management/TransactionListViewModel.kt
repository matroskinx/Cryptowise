package com.kvladislav.cryptowise.screens.transaction_management

import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject

class TransactionListViewModel : BaseViewModel(), KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    val allTransactions = liveData(Dispatchers.IO) {
        emitSource(transactionRepository.allTransactions)
    }
}