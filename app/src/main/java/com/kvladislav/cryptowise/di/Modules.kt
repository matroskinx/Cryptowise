package com.kvladislav.cryptowise.di

import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.database.TransactionDatabase
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import com.kvladislav.cryptowise.repositories.TransactionRepository
import com.kvladislav.cryptowise.screens.overview.OverviewViewModel
import com.kvladislav.cryptowise.screens.transaction.AddViewModel
import com.kvladislav.cryptowise.screens.transaction.TransactionListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel<OverviewViewModel>()
    viewModel<TransactionListViewModel>()
    viewModel<AddViewModel>()
    single { CurrencyRepository() }
    single { Preferences(get()) }
    single { TransactionDatabase.getDatabase(androidApplication()) }
    factory { get<TransactionDatabase>().transactionDao() }

    single { TransactionRepository() }
}
