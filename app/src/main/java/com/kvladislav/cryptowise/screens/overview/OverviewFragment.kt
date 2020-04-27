package com.kvladislav.cryptowise.screens.overview

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.format
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.cmc_listings.CMCListingsResponse
import com.kvladislav.cryptowise.models.cmc_listings.ListingItem
import com.kvladislav.cryptowise.screens.transaction.TransactionListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.currency_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber

class OverviewFragment : BaseFragment(R.layout.fragment_overview) {

    lateinit var adapter: ListDelegationAdapter<List<ListingItem>>

    override fun viewModel(): OverviewViewModel = getSharedViewModel()

    override fun setupView() {
        setupAdapter()
    }

    override fun setupObservers() {
        viewModel().currencyListings.observe(viewLifecycleOwner) { fillAdapterData(it) }
        viewModel().favouriteList.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun fillAdapterData(listings: CMCListingsResponse) {
        setLoadedView()
        Timber.d("Got listings")
        adapter.items = listings.data?.sortedWith(compareBy { it.cmcRank })
        adapter.notifyDataSetChanged()
    }

    override fun setupListeners() {
        transactions_btn.setOnClickListener {
            parentFragmentManager.transaction {
                this.addToBackStack(TransactionListFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, TransactionListFragment())
            }
        }
    }

    private fun setLoadedView() {
        progress_bar.visibility = View.GONE
        currency_rv.visibility = View.VISIBLE
    }

    private fun setupAdapter() {
        val newCurrencyAdapter =
            adapterDelegateLayoutContainer<ListingItem, ListingItem>(R.layout.currency_rv_item) {
                item_ll.setOnClickListener {
                    viewModel().onCurrencySelected(this.item)
                }

                favourite_button.setOnClickListener {
                    viewModel().onFavouriteCurrencyTap(this.item)
                }

                bind {
                    name.text = this.item.symbol
                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${this.item.id}.png")
                        .into(logo)
                    price_tv.text = this.item.quote?.USD?.price?.format(2)

                    if (viewModel().favouriteList.value?.contains(this.item.id) == true) {
                        favourite_button.setImageResource(R.drawable.ic_favorite)
                    } else {
                        favourite_button.setImageResource(R.drawable.ic_favorite_border)
                    }

                    val percentChange: Double? = this.item.quote?.USD?.percentChange24h?.also {
                        when {
                            it < 0 -> price_change_tv.setTextColor(getColor(R.color.red))
                            it > 0 -> price_change_tv.setTextColor(getColor(R.color.green))
                            else -> price_change_tv.setTextColor(getColor(android.R.color.tab_indicator_text))
                        }
                    }

                    price_change_tv.text = "${percentChange?.format(2)}%"
                }
            }
        adapter = ListDelegationAdapter<List<ListingItem>>(newCurrencyAdapter)
        currency_rv.addItemDecoration(
            DividerItemDecoration(
                currency_rv.context,
                DividerItemDecoration.VERTICAL
            )
        )
        currency_rv.layoutManager = LinearLayoutManager(context)
        currency_rv.adapter = adapter
    }
}