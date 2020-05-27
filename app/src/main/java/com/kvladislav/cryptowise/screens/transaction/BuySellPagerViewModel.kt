package com.kvladislav.cryptowise.screens.transaction

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.models.BuySellForm
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.portfolio.PortfolioItem
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.repositories.PortfolioRepository
import com.kvladislav.cryptowise.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class BuySellPagerViewModel(private val context: Context, val cmcData: CMCDataMinified) :
    BaseViewModel(), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()
    private val portfolioRepository: PortfolioRepository by inject()
    var currentType: TransactionType = TransactionType.BUY

    fun onActionTap(createModel: BuySellForm) {
        val transaction = BuySellTransaction(
            0,
            currentType,
            createModel.timestamp,
            createModel.price,
            createModel.quantity,
            createModel.fee,
            cmcData.cmcId,
            cmcData.coinCapId,
            cmcData.symbol,
            ""
        )
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.addTransaction(transaction)
            portfolioRepository.getAssets().run {
                Timber.d("All assets: $this")
                val item = this.find { it.coinCapId == cmcData.coinCapId }
                if (item != null) {
                    Timber.d("Updating asset $item")
                    if (currentType == TransactionType.BUY) {
                        item.assetAmount += transaction.coinQuantity
                    } else {
                        item.assetAmount -= transaction.coinQuantity
                    }
                    portfolioRepository.updateAsset(item)
                } else {
                    Timber.d("Adding asset")
                    portfolioRepository.addAsset(
                        PortfolioItem(
                            cmcData.coinCapId,
                            cmcData.cmcId,
                            cmcData.symbol,
                            createModel.quantity
                        )
                    )
                }
            }

            withActivity {
                Toast.makeText(
                    it,
                    context.getString(R.string.transaction_success),
                    Toast.LENGTH_LONG
                ).show()
                it.supportFragmentManager.popBackStack()
            }
        }
    }

    fun onOperationSet(type: TransactionType) {
        currentType = type
    }
}