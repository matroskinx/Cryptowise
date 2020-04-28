package com.kvladislav.cryptowise.screens.transaction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_add.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber
import java.lang.IllegalStateException


class AddFragment : BaseFragment(R.layout.fragment_add) {
    override fun viewModel(): AddViewModel = getSharedViewModel()

    override fun setupView() {
        transaction_viewpager.isUserInputEnabled = false
        activity?.let {
            transaction_viewpager.adapter = TransactionAdapter(it)
        }
    }

    override fun setupListeners() {
        transaction_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.d("Tab reselected: ${tab?.position}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Timber.d("Tab unselected: ${tab?.position}")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    transaction_viewpager.currentItem = it.position
                }
                Timber.d("Tab selected: ${tab?.position}")
            }
        })
    }

    inner class TransactionAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                BUY_SELL_PAGE -> BuySellPagerFragment()
                TRANSFER_PAGE -> TransferPagerFragment()
                else -> throw IllegalStateException("Incorrect page $position")
            }
        }
    }

    companion object {
        const val NUM_PAGES = 2
        const val BUY_SELL_PAGE = 0
        const val TRANSFER_PAGE = 1
    }

}