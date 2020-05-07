package com.kvladislav.cryptowise.screens.transaction

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
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
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.transaction_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class TransactionListFragment : BaseFragment(R.layout.fragment_transaction_list) {
    private lateinit var adapter: ListDelegationAdapter<List<BuySellTransaction>>

    override fun viewModel(): TransactionListViewModel = getViewModel()

    override fun setupView() {
        setupAdapter()
        setupPieChart()
    }

    private fun setupPieChart() {
        pie_chart.setUsePercentValues(true)
        pie_chart.description.isEnabled = false
        pie_chart.setExtraOffsets(5f, 10f, 5f, 5f)

        pie_chart.dragDecelerationFrictionCoef = 0.95f

        pie_chart.centerText = "ABC"

        pie_chart.isDrawHoleEnabled = true
        pie_chart.setHoleColor(Color.TRANSPARENT)

        pie_chart.setTransparentCircleColor(Color.TRANSPARENT)
        pie_chart.setTransparentCircleAlpha(110)

        pie_chart.holeRadius = 32f
        pie_chart.transparentCircleRadius = 32f

        pie_chart.setDrawCenterText(true)

        pie_chart.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pie_chart.isRotationEnabled = true
        pie_chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        pie_chart.animateY(1400, Easing.EaseInOutQuad)

        val l: Legend = pie_chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        pie_chart.setEntryLabelColor(Color.WHITE)
        pie_chart.setEntryLabelTextSize(12f)
    }

    fun setupChartData() {
        val entries = mutableListOf<PieEntry>()
        for (i in 1..3) {
            entries.add(i - 1, PieEntry(i * 10f))
        }

        val pieDataSet = PieDataSet(entries, "Pieeee")

        pieDataSet.setDrawIcons(false)

        pieDataSet.sliceSpace = 3f
        pieDataSet.iconsOffset = MPPointF(0f, 40f)
        pieDataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        pieDataSet.colors = colors
        val data = PieData(pieDataSet)
        data.setValueFormatter(PercentFormatter(pie_chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pie_chart.data = data
        pie_chart.highlightValues(null)
        pie_chart.invalidate()
    }

    override fun setupObservers() {
        viewModel().allTransactions.observe(viewLifecycleOwner) {
            setupChartData()
            Timber.d("Triggered observer: $it")
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun formatDate(timestamp: Long): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return fmt.format(timestamp)
    }

    private fun setupAdapter() {
        val transactionAdapter =
            adapterDelegateLayoutContainer<BuySellTransaction, BuySellTransaction>(R.layout.transaction_rv_item) {
                bind {
                    val item = this.item
                    operation_tv.text = item.type.getFriendlyName(context)
                    quantity_tv.text = "${item.coinQuantity} ${item.cmcSymbol}"
                    date_tv.text = formatDate(item.timestamp)

                    val operation = when (item.type) {
                        TransactionType.BUY -> "Paid"
                        TransactionType.SELL -> "Received"
                        TransactionType.TRANSFER -> "Transferred"
                    }

                    usd_tv.text = "$operation ${item.usdPerCoin}$"

                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${item.cmcId}.png")
                        .into(operation_iv)
                }
            }

        adapter = ListDelegationAdapter<List<BuySellTransaction>>(transactionAdapter)
        transaction_rv.layoutManager = LinearLayoutManager(context)
        transaction_rv.adapter = adapter
    }
}