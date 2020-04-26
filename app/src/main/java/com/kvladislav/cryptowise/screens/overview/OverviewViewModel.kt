package com.kvladislav.cryptowise.screens.overview

import android.content.Context
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.repositories.OverviewRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject

class OverviewViewModel(private val context: Context) : BaseViewModel(), KoinComponent {
    private val currencyRepository: OverviewRepository by inject()
    val currencyList = liveData(Dispatchers.IO) {
        emit(currencyRepository.getIDMap())
    }
}