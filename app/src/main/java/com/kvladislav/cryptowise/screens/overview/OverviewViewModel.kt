package com.kvladislav.cryptowise.screens.overview

import android.content.Context
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.cmc_listings.ListingItem
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class OverviewViewModel(private val context: Context) : BaseViewModel(), KoinComponent {
    private val currencyRepository: CurrencyRepository by inject()

    val currencyListings = liveData(Dispatchers.IO) {
        emit(currencyRepository.getListings())
    }

    fun onCurrencySelected(item: ListingItem) {
        Timber.d("Selected item: ${item.id} ${item.symbol}")
    }

    fun onFavouriteCurrencyTap(item: ListingItem) {
        Timber.d("Favourite item: ${item.id} ${item.symbol}")
    }
}