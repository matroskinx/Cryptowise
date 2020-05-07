package com.kvladislav.cryptowise.screens.overview

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.CurrencySetWrapper
import com.kvladislav.cryptowise.screens.currency.CurrencyDetailsFragment
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class OverviewViewModel(private val context: Context) : BaseViewModel(), KoinComponent {
    private val preferences: Preferences by inject()

    val favouriteList: MutableLiveData<Set<Int>> = MutableLiveData()

    init {
        favouriteList.value = preferences.getFavouriteCurrencies().ids
    }

    fun onCurrencySelected(item: CombinedAssetModel) {
        withActivity {
            it.supportFragmentManager.transaction {
                addToBackStack(CurrencyDetailsFragment::class.java.canonicalName)
                replace(R.id.fragment_container, CurrencyDetailsFragment.build(item))
            }
        }
    }

    fun onFavouriteCurrencyTap(item: CombinedAssetModel) {
        if (item.cmcMapItem.id == null) return
        preferences.getFavouriteCurrencies().ids.run {
            val newIds = if (this.contains(item.cmcMapItem.id)) {
                this.minus(item.cmcMapItem.id)
            } else {
                this.plus(item.cmcMapItem.id)
            }
            preferences.setFavouriteCurrencies(CurrencySetWrapper(newIds))
            favouriteList.postValue(newIds)
        }
        Timber.d("Favourite item: ${item.cmcMapItem.id} ${item.cmcMapItem.symbol}")
    }
}