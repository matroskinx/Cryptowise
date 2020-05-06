package com.kvladislav.cryptowise.repositories

import androidx.lifecycle.LiveData
import com.kvladislav.cryptowise.database.dao.TransactionDao
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import org.koin.core.KoinComponent
import org.koin.core.inject

class TransactionRepository : KoinComponent {
    private val transactionDao: TransactionDao by inject()
    val allTransactions: LiveData<List<BuySellTransaction>> = transactionDao.getTransactions()

    suspend fun addTransaction(transaction: BuySellTransaction) {
        transactionDao.insert(transaction)
    }
}