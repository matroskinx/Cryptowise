package com.kvladislav.cryptowise.screens.transaction

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class AddViewModel(private val context: Context) : BaseViewModel(), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()
    var currentType: TransactionType = TransactionType.BUY

    fun onActionTap(createModel: BuySellForm) {
        val transaction = BuySellTransaction(
            0,
            createModel.timestamp,
            createModel.price,
            createModel.quantity,
            createModel.fee,
            1,
            "BTC",
            ""
        )
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun onOperationSet(type: TransactionType) {
        currentType = type
    }
}