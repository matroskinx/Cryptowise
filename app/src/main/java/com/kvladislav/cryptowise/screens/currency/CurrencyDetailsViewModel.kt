package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class CurrencyDetailsViewModel(
    private val context: Context,
    private val cmcData: CMCDataMinified
) : BaseViewModel(), KoinComponent {
    init {
        Timber.d("INIIIT: $cmcData")
    }

    private val coinCapRepository: CoinCapRepository by inject()

    val candlesData = liveData {
        emit(coinCapRepository.getCandles("bitcoin"))
    }

    fun onAddTransactionTap() {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(AddFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, AddFragment.build(cmcData))
            }
        }
    }
}
