package com.kvladislav.cryptowise.screens.transaction

import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class TransferPagerFragment : BaseFragment(R.layout.fragment_transfer_pager) {
    override fun viewModel(): AddViewModel = getSharedViewModel()
}