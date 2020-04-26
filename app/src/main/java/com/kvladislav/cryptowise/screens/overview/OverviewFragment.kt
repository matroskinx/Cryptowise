package com.kvladislav.cryptowise.screens.overview

import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.cmc_map.CMCIDMapResponse
import com.kvladislav.cryptowise.models.cmc_map.DataItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.mock_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class OverviewFragment : BaseFragment(R.layout.fragment_overview) {

    lateinit var adapter: ListDelegationAdapter<List<DataItem>>

    override fun viewModel(): OverviewViewModel = getSharedViewModel()

    override fun setupView() {
        setupAdapter()
    }

    override fun setupObservers() {
        viewModel().currencyList.observe(viewLifecycleOwner) { fillAdapterData(it) }
    }

    private fun fillAdapterData(data: CMCIDMapResponse) {
        adapter.items = data.data
        adapter.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        val currencyAdapter =
            adapterDelegateLayoutContainer<DataItem, DataItem>(R.layout.mock_item) {
                bind {
                    name.text = this.item.name
                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${this.item.id}.png")
                        .into(logo)
                }
            }

        adapter = ListDelegationAdapter<List<DataItem>>(currencyAdapter)
        currency_rv.layoutManager = LinearLayoutManager(context)
        currency_rv.adapter = adapter
    }
}