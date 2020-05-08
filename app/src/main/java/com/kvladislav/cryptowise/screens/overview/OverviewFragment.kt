package com.kvladislav.cryptowise.screens.overview

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.formatDigits
import com.kvladislav.cryptowise.extensions.formatWithPercent
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.screens.transaction.TransactionListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.currency_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class OverviewFragment : BaseFragment(R.layout.fragment_overview) {

    private lateinit var adapter: ListDelegationAdapter<List<CombinedAssetModel>>
    private lateinit var appViewModel: AppViewModel

    override fun viewModel(): OverviewViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = getSharedViewModel()
    }

    override fun setupView() {
        setupAdapter()
    }

    override fun setupObservers() {
        appViewModel.currencyListings.observe(viewLifecycleOwner) {
            appViewModel.tryUpdatePortfolio()
            fillAdapterData(it)
        }
        viewModel().favouriteList.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
        appViewModel.portfolioAssets.observe(viewLifecycleOwner) {
            appViewModel.tryUpdatePortfolio()
            adapter.notifyDataSetChanged()
            Timber.d("Portfolio: $it")
        }

        appViewModel.fullPortfolio.observe(viewLifecycleOwner) {
            Timber.d("Sum: $it")
            val text = it.value.formatDigits(2) + "$"
            adapter.notifyDataSetChanged()
            portfolio_value_tv.text = text
        }
    }

    private fun fillAdapterData(listings: List<CombinedAssetModel>) {
        setLoadedView()
        Timber.d("Got listings")
        adapter.items = listings
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

    private fun setupHoldingsValue(coinCapId: String, assetValue: Double): String {
        val dashString = getString(R.string.dash)
        val portfolio = appViewModel.fullPortfolio.value ?: return dashString

        if (portfolio.value == 0.0) {
            return dashString
        }
        val portfolioAsset = appViewModel.portfolioAssets.value?.find {
            it.coinCapId == coinCapId
        } ?: return dashString

        val percent = (portfolioAsset.assetAmount * assetValue) / portfolio.value * 100
        return "${percent.formatDigits(4)}%"
    }

    private fun setupAdapter() {
        val newCurrencyAdapter =
            adapterDelegateLayoutContainer<CombinedAssetModel, CombinedAssetModel>(R.layout.currency_rv_item) {
                item_ll.setOnClickListener {
                    viewModel().onCurrencySelected(this.item)
                }

                favourite_button.setOnClickListener {
                    viewModel().onFavouriteCurrencyTap(this.item)
                }

                bind {
                    name.text = this.item.cmcMapItem.symbol
                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${this.item.cmcMapItem.id}.png")
                        .into(logo)
                    price_tv.text = this.item.coinCapAssetItem.priceUsd

                    if (viewModel().favouriteList.value?.contains(this.item.cmcMapItem.id) == true) {
                        favourite_button.setImageResource(R.drawable.ic_favorite)
                    } else {
                        favourite_button.setImageResource(R.drawable.ic_favorite_border)
                    }

                    val change24h: Double? =
                        this.item.coinCapAssetItem.changePercent24Hr?.toDouble()

                    change24h?.also {
                        when {
                            it < 0 -> price_change_tv.setTextColor(getColor(R.color.red))
                            it > 0 -> price_change_tv.setTextColor(getColor(R.color.green))
                            else -> price_change_tv.setTextColor(getColor(android.R.color.tab_indicator_text))
                        }

                        price_change_tv.text = it.formatWithPercent(2)
                    }

                    holdings_tv.text = setupHoldingsValue(
                        this.item.coinCapAssetItem.id!!,
                        this.item.coinCapAssetItem.priceUsd!!.toDouble()
                    )

                }
            }
        adapter = ListDelegationAdapter<List<CombinedAssetModel>>(newCurrencyAdapter)
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