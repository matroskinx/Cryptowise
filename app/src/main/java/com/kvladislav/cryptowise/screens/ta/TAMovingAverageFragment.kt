package com.kvladislav.cryptowise.screens.ta

import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TAMovingAverageFragment : BaseFragment(R.layout.fragment_ta_moving_average) {
    override fun viewModel(): TAMovingAverageViewModel = getViewModel()
}