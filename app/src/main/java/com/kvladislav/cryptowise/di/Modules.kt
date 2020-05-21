package com.kvladislav.cryptowise.di

import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.Preferences
import com.kvladislav.cryptowise.database.TransactionDatabase
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import com.kvladislav.cryptowise.repositories.PortfolioRepository
import com.kvladislav.cryptowise.repositories.TransactionRepository
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.screens.authorization.login.LoginViewModel
import com.kvladislav.cryptowise.screens.authorization.registration.RegistrationViewModel
import com.kvladislav.cryptowise.screens.currency.CurrencyDetailsViewModel
import com.kvladislav.cryptowise.screens.overview.OverviewViewModel
import com.kvladislav.cryptowise.screens.transaction.BuySellPagerViewModel
import com.kvladislav.cryptowise.screens.portfolio.PortfolioViewModel
import com.kvladislav.cryptowise.screens.splash.SplashViewModel
import com.kvladislav.cryptowise.screens.ta.TAMovingAverageViewModel
import com.kvladislav.cryptowise.screens.transaction_management.TransactionListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel<OverviewViewModel>()
    viewModel<PortfolioViewModel>()
    viewModel<RegistrationViewModel>()
    viewModel<LoginViewModel>()
    viewModel<SplashViewModel>()
    viewModel { (appVM: AppViewModel) -> TAMovingAverageViewModel(get(), appVM) }
    viewModel<TransactionListViewModel>()
    viewModel { (data: CMCDataMinified) -> BuySellPagerViewModel(get(), data) }
    viewModel { (appVM: AppViewModel, data: CMCDataMinified) ->
        CurrencyDetailsViewModel(
            get(),
            appVM,
            data
        )
    }
    viewModel<AppViewModel>()
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
