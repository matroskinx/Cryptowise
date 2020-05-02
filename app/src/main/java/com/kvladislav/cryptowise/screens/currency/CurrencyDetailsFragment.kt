package com.kvladislav.cryptowise.screens.currency

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import kotlinx.android.synthetic.main.fragment_currency_details.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class CurrencyDetailsFragment : BaseFragment(R.layout.fragment_currency_details) {
    override fun viewModel(): CurrencyDetailsViewModel =
        getViewModel { parametersOf(parseArguments(arguments)) }


    private fun parseArguments(bundle: Bundle?): CMCDataMinified {
        return bundle?.run {
            CMCDataMinified(
                getInt(CMC_ID_EXTRA),
                getString(CMC_SYMBOL_EXTRA, ""),
                getString(COINCAP_ID_EXTRA, "")
            )
        } ?: throw IllegalArgumentException("Bundle was null during initialization")
    }

    override fun setupListeners() {
        add_tr_button.setOnClickListener { viewModel().onAddTransactionTap() }

        day.setOnClickListener {
            viewModel().onIntervalChange(CurrencyDetailsViewModel.TimeInterval.DAY)
        }

        week.setOnClickListener {
            viewModel().onIntervalChange(CurrencyDetailsViewModel.TimeInterval.WEEK)
        }

        month.setOnClickListener {
            viewModel().onIntervalChange(CurrencyDetailsViewModel.TimeInterval.MONTH)
        }

        month_3.setOnClickListener {
            viewModel().onIntervalChange(CurrencyDetailsViewModel.TimeInterval.MONTH_3)
        }

        year.setOnClickListener {
            viewModel().onIntervalChange(CurrencyDetailsViewModel.TimeInterval.YEAR)
        }
    }

    override fun setupObservers() {
        viewModel().candlesData.observe(viewLifecycleOwner) {
            it.data?.let { candles ->
                fillChartWithData(candles)
            }
        }
    }

    override fun setupView() {
        setupChart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel().requestCandles()
    }

    private fun fillChartWithData(items: List<CandleItem?>) {
        Timber.d("Candles amount: ${items.count()}")
        if (items.count() == 0) {
            Toast.makeText(context, "There is no candle data for this market!", Toast.LENGTH_LONG)
                .show()
            return
        }
        val start = items[0]?.period!! / 1000000
        val candles = mutableListOf<CandleEntry>()
        var counter = 1
        for (candle in items) {
            candles.add(
                CandleEntry(
                    counter.toFloat(),
//                    candle?.period?.toFloat()?.div(1000000)?.minus(start) ?: 0f,
                    candle?.high?.toFloat() ?: 0f,
                    candle?.low?.toFloat() ?: 0f,
                    candle?.open?.toFloat() ?: 0f,
                    candle?.close?.toFloat() ?: 0f
                )
            )
            counter++
        }

        val dataSet = CandleDataSet(candles, "btc")

        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.shadowColor = Color.DKGRAY
        dataSet.shadowWidth = 0.7f
        dataSet.decreasingColor = Color.RED
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = Color.rgb(122, 242, 84)
        dataSet.increasingPaintStyle = Paint.Style.FILL_AND_STROKE
        dataSet.neutralColor = Color.BLUE

        val data = CandleData(dataSet)

        ohlcv_chart.data = data
        ohlcv_chart.invalidate()
    }

    private fun setupChart() {
        val candleStickChart: CandleStickChart = ohlcv_chart
        candleStickChart.setBackgroundColor(Color.WHITE)
        candleStickChart.description.isEnabled = false
        candleStickChart.setPinchZoom(false)
        candleStickChart.setDrawGridBackground(false)
        val xAxis = candleStickChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(2, true)
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false


        val leftAxis = candleStickChart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = candleStickChart.axisRight
        rightAxis.isEnabled = false

        candleStickChart.legend.isEnabled = false
    }

    companion object {
        const val CMC_ID_EXTRA = "CMC_ID_EXTRA"
        const val CMC_SYMBOL_EXTRA = "CMC_SYMBOL_EXTRA"
        const val COINCAP_ID_EXTRA = "COINCAP_ID_EXTRA"

        fun build(item: CombinedAssetModel): CurrencyDetailsFragment {
            return CurrencyDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(
                        CMC_ID_EXTRA,
                        item.cmcMapItem.id ?: throw IllegalStateException("Id is empty")
                    )
                    putString(CMC_SYMBOL_EXTRA, item.cmcMapItem.symbol ?: "")
                    putString(COINCAP_ID_EXTRA, item.coinCapAssetItem.id ?: "")
                }
            }
        }
    }
}