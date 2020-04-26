package com.kvladislav.cryptowise.di

import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import com.kvladislav.cryptowise.screens.overview.OverviewViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel<OverviewViewModel>()
    single { CurrencyRepository() }
    single { Preferences(get()) }
}
