package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import timber.log.Timber

class CurrencyDetailsViewModel(
    private val context: Context,
    private val cmcData: CMCDataMinified
) : BaseViewModel() {
    init {
        Timber.d("INIIIT: $cmcData")
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
