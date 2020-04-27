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


class AddFragment : BaseFragment(R.layout.fragment_add) {
    override fun viewModel(): AddViewModel = getSharedViewModel()

    override fun setupView() {
        transaction_viewpager.isUserInputEnabled = false
        activity?.let {
            transaction_viewpager.adapter = TransactionAdapter(it)
        }
    }

    override fun setupListeners() {
        add_t_button.setOnClickListener { viewModel().createAndAddTransaction() }

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
}

class TransactionAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else -> TODO("fill in fragments")
        }
    }

}