package com.kvladislav.cryptowise.screens.currency

import android.os.Bundle
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.cmc_listings.ListingItem
import kotlinx.android.synthetic.main.fragment_currency_details.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class CurrencyDetailsFragment : BaseFragment(R.layout.fragment_currency_details) {
    override fun viewModel(): CurrencyDetailsViewModel =
        getViewModel { parametersOf(parseArguments(arguments)) }


    private fun parseArguments(bundle: Bundle?): CMCDataMinified {
        return bundle?.run {
            CMCDataMinified(getInt(CMC_ID_EXTRA), getString(CMC_SYMBOL_EXTRA, ""))
        } ?: throw IllegalArgumentException("Bundle was null during initialization")
    }

    override fun setupListeners() {
        add_tr_button.setOnClickListener { viewModel().onAddTransactionTap() }
    }

    companion object {
        const val CMC_ID_EXTRA = "CMC_ID_EXTRA"
        const val CMC_SYMBOL_EXTRA = "CMC_SYMBOL_EXTRA"

        fun build(item: ListingItem): CurrencyDetailsFragment {
            return CurrencyDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(CMC_ID_EXTRA, item.id ?: throw IllegalStateException("Id is empty"))
                    putString(CMC_SYMBOL_EXTRA, item.symbol ?: "")
                }
            }
        }
    }
}