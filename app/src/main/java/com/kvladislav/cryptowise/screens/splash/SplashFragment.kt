package com.kvladislav.cryptowise.screens.splash

import android.os.Bundle
import android.view.View
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SplashFragment : BaseFragment(R.layout.fragment_splash) {
    override fun viewModel(): SplashViewModel = getViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel().handleLaunch()
    }
}