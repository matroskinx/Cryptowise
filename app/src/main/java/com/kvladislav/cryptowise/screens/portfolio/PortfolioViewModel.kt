package com.kvladislav.cryptowise.screens.portfolio

import android.content.Context
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.transaction_management.TransactionListFragment
import org.koin.core.KoinComponent

class PortfolioViewModel(private val context: Context) : BaseViewModel(), KoinComponent {
    fun onTransactionsButtonTap() {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(TransactionListFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, TransactionListFragment())
            }
        }
    }
}