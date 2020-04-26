package com.kvladislav.cryptowise.screens.overview

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.CurrencySetWrapper
import com.kvladislav.cryptowise.models.cmc_listings.ListingItem
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class OverviewViewModel(private val context: Context) : BaseViewModel(), KoinComponent {
    private val currencyRepository: CurrencyRepository by inject()
    private val preferences: Preferences by inject()
    val favouriteList: MutableLiveData<Set<Int>> = MutableLiveData()

    init {
        favouriteList.value = preferences.getFavouriteCurrencies().ids
    }

    val currencyListings = liveData(Dispatchers.IO) {
        emit(currencyRepository.getListings())
    }

    fun onCurrencySelected(item: ListingItem) {
        Timber.d("Selected item: ${item.id} ${item.symbol}")
    }

    fun onFavouriteCurrencyTap(item: ListingItem) {
        if (item.id == null) return
        preferences.getFavouriteCurrencies().ids.run {
            val newIds = if (this.contains(item.id)) {
                this.minus(item.id)
            } else {
                this.plus(item.id)
            }
            preferences.setFavouriteCurrencies(CurrencySetWrapper(newIds))
            favouriteList.postValue(newIds)
        }
        Timber.d("Favourite item: ${item.id} ${item.symbol}")
    }
}