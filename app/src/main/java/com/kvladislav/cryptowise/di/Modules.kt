package com.kvladislav.cryptowise.di

import com.kvladislav.cryptowise.repositories.OverviewRepository
import com.kvladislav.cryptowise.screens.overview.OverviewViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel<OverviewViewModel>()
    single { OverviewRepository() }
}
