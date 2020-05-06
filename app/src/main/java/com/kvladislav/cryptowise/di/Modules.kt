package com.kvladislav.cryptowise.di

import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.database.TransactionDatabase
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import com.kvladislav.cryptowise.repositories.PortfolioRepository
import com.kvladislav.cryptowise.repositories.TransactionRepository
import com.kvladislav.cryptowise.screens.currency.CurrencyDetailsViewModel
import com.kvladislav.cryptowise.screens.overview.OverviewViewModel
import com.kvladislav.cryptowise.screens.transaction.BuySellPagerViewModel
import com.kvladislav.cryptowise.screens.transaction.TransactionListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel<OverviewViewModel>()
    viewModel<TransactionListViewModel>()
    viewModel { (data: CMCDataMinified) -> BuySellPagerViewModel(get(), data) }
    viewModel { (data: CMCDataMinified) -> CurrencyDetailsViewModel(get(), data) }
    single { CurrencyRepository() }
    single { CoinCapRepository() }
    single { Preferences(get()) }
    single { TransactionDatabase.getDatabase(androidApplication()) }
    single { DataStorage() }
    factory { get<TransactionDatabase>().transactionDao() }
    factory { get<TransactionDatabase>().portfolioDao() }
    single { TransactionRepository() }
    single { PortfolioRepository() }
}
