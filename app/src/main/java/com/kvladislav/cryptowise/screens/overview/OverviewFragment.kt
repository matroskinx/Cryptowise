package com.kvladislav.cryptowise.screens.overview

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.formatDigits
import com.kvladislav.cryptowise.extensions.formatWithPercentSigned
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.screens.portfolio.PortfolioFragment
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
        setHasOptionsMenu(true)
        appViewModel = getSharedViewModel()
    }

    override fun setupView() {
        setupAdapter()
        setLoadingView()
    }

    override fun setupObservers() {
        appViewModel.assetListings.observe(viewLifecycleOwner) {
            appViewModel.tryUpdatePortfolio()
            fillAdapterDataDependingOnFavs(appViewModel.isShowingFavourites.value ?: false)
        }
        viewModel().favouriteList.observe(viewLifecycleOwner) {
            fillAdapterDataDependingOnFavs(appViewModel.isShowingFavourites.value ?: false)
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

        appViewModel.connectionErrorLiveData.observe(viewLifecycleOwner) {
            it?.run {
                Timber.d("Trigger no connection LiveData")
                setNoConnectionView()
            }
        }

        appViewModel.isShowingFavourites.observe(viewLifecycleOwner) { isShowing ->
            fillAdapterDataDependingOnFavs(isShowing)
        }
    }

    private fun fillAdapterDataDependingOnFavs(isShowing: Boolean) {
        Timber.d("is showing favourites: $isShowing")
        val favourites = viewModel().favouriteList.value ?: return
        val listings = appViewModel.assetListings.value ?: return

        if (isShowing) {
            val filteredListings = listings.filter { favourites.contains(it.cmcId) }
            fillAdapterData(filteredListings)
            setNoFavouriteAssetsView(filteredListings.isEmpty())
        } else {
            fillAdapterData(listings)
            setNoFavouriteAssetsView(false)
        }
        changeFavouriteIcon(isShowing)
    }

    private fun changeFavouriteIcon(showing: Boolean) {
        if (showing) {
            fav_btn.setImageResource(R.drawable.ic_favorite)
        } else {
            fav_btn.setImageResource(R.drawable.ic_favorite_border)
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
                this.addToBackStack(PortfolioFragment::class.java.canonicalName)
                this.replace(
                    R.id.fragment_container,
                    PortfolioFragment()
                )
            }
        }

        refresh_button.setOnClickListener {
            setLoadingView()
            appViewModel.tryRefreshListings()
        }

        fav_btn.setOnClickListener {
            appViewModel.onShowFavouritesTap()
        }
    }

    private fun setLoadedView() {
        progress_bar.isVisible = false
        no_connection_layout.isVisible = false
        currency_rv.isVisible = true
        portfolio_panel.isVisible = true
    }

    private fun setLoadingView() {
        progress_bar.isVisible = true
        no_connection_layout.isVisible = false
        currency_rv.isVisible = false
        portfolio_panel.isVisible = false
    }

    private fun setNoConnectionView() {
        progress_bar.isVisible = false
        no_connection_layout.isVisible = true
        currency_rv.isVisible = false
        portfolio_panel.isVisible = false
    }

    private fun setNoFavouriteAssetsView(isVisible: Boolean) {
        no_favs_layout.isVisible = isVisible
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

                    val priceText = "${this.item.coinCapAssetItem.priceUsd?.formatDigits(3)}$"

                    price_tv.text = priceText

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

                        price_change_tv.text = it.formatWithPercentSigned(2)
                    }

                    holdings_tv.text = setupHoldingsValue(
                        this.item.coinCapAssetItem.id!!,
                        this.item.coinCapAssetItem.priceUsd!!.toDouble()
                    )

                }
            }
        adapter = ListDelegationAdapter(newCurrencyAdapter)
        currency_rv.addItemDecoration(
            DividerItemDecoration(
                currency_rv.context,
                DividerItemDecoration.VERTICAL
            )
        )
        currency_rv.layoutManager = LinearLayoutManager(context)
        currency_rv.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> showLogoutDialog()
        }
        return true
    }

    private fun showLogoutDialog() {
        context?.run {
            MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure you want to log out?")
                .setPositiveButton("Yes") { dialog, which ->
                    Timber.d("Yes $dialog $which")
                    viewModel().logoutAndLeave()
                }.setNegativeButton("No") { _, _ -> }.show()
        }
    }
}