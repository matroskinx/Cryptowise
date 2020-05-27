package com.kvladislav.cryptowise.screens.transaction_management

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.repositories.PortfolioRepository
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class TransactionListViewModel : BaseViewModel(), KoinComponent {
    private val transactionRepository: TransactionRepository by inject()

    private val portfolioRepository: PortfolioRepository by inject()

    val allTransactions = liveData(Dispatchers.IO) {
        emitSource(transactionRepository.allTransactions)
    }

    fun onItemClicked(item: BuySellTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.removeTransaction(item.id)

            portfolioRepository.allAssets.value?.run {
                this.find { portfolioItem ->
                    portfolioItem.coinCapId == item.coinCapId
                }?.run {
                    Timber.d("Found asset: $this")
                    val multiplier = when (item.type) {
                        TransactionType.BUY -> -1
                        TransactionType.SELL -> 1
                        TransactionType.TRANSFER -> 0
                    }
                    this.assetAmount += multiplier * item.coinQuantity
                    Timber.d("After change ${this.assetAmount}")
                    if (this.assetAmount <= 0.00000001) {
                        portfolioRepository.removeAsset(this)
                    } else {
                        portfolioRepository.updateAsset(this)
                    }
                } ?: Timber.d("Did not found asset")
            }
        }
    }
}