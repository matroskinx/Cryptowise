package com.kvladislav.cryptowise.screens.portfolio

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.formatDigits
import com.kvladislav.cryptowise.extensions.formatWithPercent
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.portfolio.DisplayPortfolioItem
import com.kvladislav.cryptowise.models.portfolio.FullPortfolio
import com.kvladislav.cryptowise.screens.AppViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_portfolio.*
import kotlinx.android.synthetic.main.portfolio_asset_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber


class PortfolioFragment : BaseFragment(R.layout.fragment_portfolio) {
    override fun viewModel(): PortfolioViewModel = getViewModel()

    private lateinit var appViewModel: AppViewModel
    private lateinit var adapter: ListDelegationAdapter<List<DisplayPortfolioItem>>

    private val colors: MutableList<Int> = mutableListOf()

    init {
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = getSharedViewModel()
    }

    override fun setupListeners() {
        transactions_btn.setOnClickListener {
            viewModel().onTransactionsButtonTap()
        }
    }

    override fun setupView() {
        setupPieChart()
        createRecyclerViewAdapter()
    }

    private fun setupPieChart() {
        pie_chart.setUsePercentValues(true)
        pie_chart.description.isEnabled = false
        pie_chart.setExtraOffsets(5f, 10f, 5f, 5f)

        pie_chart.dragDecelerationFrictionCoef = 0.95f

        pie_chart.isDrawHoleEnabled = true
        pie_chart.setHoleColor(Color.TRANSPARENT)

        pie_chart.setCenterTextColor(Color.WHITE)
        pie_chart.setCenterTextSize(16f)

        pie_chart.holeRadius = 95f
        pie_chart.transparentCircleRadius = 60f

        pie_chart.setDrawCenterText(true)

        pie_chart.rotationAngle = 90f
        pie_chart.isRotationEnabled = true
        pie_chart.isHighlightPerTapEnabled = true

        pie_chart.animateY(1400, Easing.EaseInOutQuad)

        pie_chart.legend.isEnabled = false
    }

    private fun setupChartData(portfolio: FullPortfolio) {
        pie_chart.centerText = "\$${portfolio.value.formatDigits(4)}"
        val entries = mutableListOf<PieEntry>()
        for (asset in portfolio.assets) {
            val assetPrice = asset.itemPrice * asset.portfolioItem.assetAmount / portfolio.value
            entries.add(PieEntry(assetPrice.toFloat()))
        }

        val pieDataSet = PieDataSet(entries, "")

        pieDataSet.setDrawIcons(false)

//        pieDataSet.sliceSpace = 3f
        pieDataSet.iconsOffset = MPPointF(0f, 40f)
        pieDataSet.selectionShift = 5f


        pieDataSet.colors = colors
        val data = PieData(pieDataSet)
        data.setValueFormatter(PercentFormatter(pie_chart))
        data.setValueTextSize(11f)
        data.setDrawValues(false)
        data.setValueTextColor(Color.WHITE)
        pie_chart.data = data
        pie_chart.highlightValues(null)
        pie_chart.invalidate()
    }

    override fun setupObservers() {
        appViewModel.fullPortfolio.observe(viewLifecycleOwner) { portfolio ->
            if (portfolio.isEmpty) {
                setupEmptyPortfolioView()
            } else {
                setupChartData(portfolio)
                setRecyclerViewData(portfolio)
                setupDefaultPortfolioView()
            }
        }
    }

    private fun setupEmptyPortfolioView() {
        portfolio_empty_layout.isVisible = true
        portolio_layout.isVisible = false
    }

    private fun setupDefaultPortfolioView() {
        portfolio_empty_layout.isVisible = false
        portolio_layout.isVisible = true
    }

    private fun getAssetPercent(holdValue: Double): Double {
        return appViewModel.fullPortfolio.value?.run {
            holdValue / this.value * 100
        } ?: 0.0
    }

    private fun createRecyclerViewAdapter() {
        val portfolioAssetAdapter =
            adapterDelegateLayoutContainer<DisplayPortfolioItem, DisplayPortfolioItem>(R.layout.portfolio_asset_rv_item) {
                bind {
                    val item = this.item

                    symbol_tv.text = item.portfolioItem.symbol
                    quantity_tv.text = item.portfolioItem.assetAmount.formatDigits(4)
                    portfolio_percent_tv.text = getAssetPercent(item.holdValue).formatWithPercent(4)
                    hold_value_tv.text =
                        "\$${(item.holdValue).formatDigits(4)}"

                    view.setBackgroundColor(colors[adapterPosition % colors.count()])

                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${item.portfolioItem.iconId}.png")
                        .into(asset_iv)
                }
            }



        adapter = ListDelegationAdapter(portfolioAssetAdapter)
        asset_rv.addItemDecoration(
            DividerItemDecoration(
                asset_rv.context,
                DividerItemDecoration.VERTICAL
            )
        )
        asset_rv.layoutManager = LinearLayoutManager(context)
        asset_rv.adapter = adapter

    }

    private fun setRecyclerViewData(portfolio: FullPortfolio) {
        adapter.items = portfolio.assets
        adapter.notifyDataSetChanged()
    }
}