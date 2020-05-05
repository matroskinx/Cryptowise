package com.kvladislav.cryptowise.screens.currency

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.utils.TAUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_currency_details.*
import kotlinx.android.synthetic.main.fragment_currency_details.crypto_iv
import kotlinx.android.synthetic.main.fragment_currency_details.crypto_tv
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class CurrencyDetailsFragment : BaseFragment(R.layout.fragment_currency_details) {
    override fun viewModel(): CurrencyDetailsViewModel =
        getViewModel { parametersOf(parseArguments(arguments)) }

    override fun setupView() {
        setupChart()
        setupVolumeChart()
        day_chip.isChecked = true

        Picasso.get()
            .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${viewModel().cmcData.id}.png")
            .into(crypto_iv)
        val str = "${viewModel().cmcData.symbol}/USDT"
        crypto_tv.text = str
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel().requestCandles()
    }


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
        chip_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.day_chip -> CurrencyDetailsViewModel.TimeInterval.DAY
                R.id.week_chip -> CurrencyDetailsViewModel.TimeInterval.WEEK
                R.id.month_chip -> CurrencyDetailsViewModel.TimeInterval.MONTH
                R.id.month_3_chip -> CurrencyDetailsViewModel.TimeInterval.MONTH_3
                R.id.month_6_chip -> CurrencyDetailsViewModel.TimeInterval.MONTH_6
                R.id.year_chip -> CurrencyDetailsViewModel.TimeInterval.YEAR
                else -> throw IllegalArgumentException("Unable to find enum value for button $checkedId")
            }.run {
                viewModel().onIntervalChange(this)
            }
        }
    }

    override fun setupObservers() {
        viewModel().chartData.observe(viewLifecycleOwner) { candles ->
            fillChartWithData(candles)
            fillVolumeChartWithData(candles)
            displaySimpleMovingAverage(candles)
        }
    }

    private fun fillChartWithData(items: List<CandleItem>) {
        Timber.d("Candles amount: ${items.count()}")
        if (items.count() == 0) {
            Toast.makeText(context, "There is no candle data for this market!", Toast.LENGTH_LONG)
                .show()
            return
        }
        val candles = mutableListOf<CandleEntry>()
        var counter = 1
        for (candle in items) {
            candles.add(
                CandleEntry(
                    counter.toFloat(),
                    candle.high?.toFloat() ?: 0f,
                    candle.low?.toFloat() ?: 0f,
                    candle.open?.toFloat() ?: 0f,
                    candle.close?.toFloat() ?: 0f
                )
            )
            counter++
        }

        val dataSet = CandleDataSet(candles, "OHLC Candles")

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

    private fun fillVolumeChartWithData(candles: List<CandleItem>) {
        if (candles.count() == 0) {
            Toast.makeText(context, "There is no candle data for this market!", Toast.LENGTH_LONG)
                .show()
            return
        }
        var start = 1f
        val values: ArrayList<BarEntry> = ArrayList()
        for (candle in candles) {
            values.add(BarEntry(start, candle.volume?.toFloat() ?: 0f))
            start++
        }
        val dataSet = BarDataSet(values, "Volume (amount of asset traded)")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.red)
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)
        val data = BarData(dataSet)
        data.setValueTextSize(8f)
        data.barWidth = 0.7f
        volume_chart.data = data
        volume_chart.invalidate()
    }

    private fun displaySimpleMovingAverage(candles: List<CandleItem>) {
        val period = 5
        val sma = TAUtils.simpleMovingAverage(candles, period)
        val values: ArrayList<Entry> = ArrayList()
        var start = 1f
        val fillDataLen = period - 1

        for (i in 0..fillDataLen) {
            values.add(Entry(start, 0f))
            start++
        }

        for (v in sma) {
            values.add(Entry(start, v))
            start++
        }

        val dataSet = LineDataSet(values, "SMA 5")
        val lineData = LineData(dataSet)

        line_chart.data = lineData
        line_chart.invalidate()
        fillInSMAS(candles)
    }

    private fun fillInSMAS(candles: List<CandleItem>) {
        val periods = mutableListOf(5, 10)
        val views = mutableListOf(sma_5, sma_10)

        for (i in 0 until periods.count()) {
            val sma = TAUtils.simpleMovingAverage(candles, periods[i])
            views[i].text = sma.last().toString()
        }

    }

    private fun setupChart() {
        val candleStickChart: CandleStickChart = ohlcv_chart
        candleStickChart.setBackgroundColor(Color.WHITE)
        candleStickChart.description.isEnabled = false
        candleStickChart.setPinchZoom(false)
        candleStickChart.setDrawGridBackground(false)
        val xAxis = candleStickChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setLabelCount(2, true)
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        val leftAxis = candleStickChart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = candleStickChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun setupVolumeChart() {
        volume_chart.setDrawBarShadow(false)
        volume_chart.description.isEnabled = false
        volume_chart.setMaxVisibleValueCount(60)
        volume_chart.setDrawValueAboveBar(false)

        volume_chart.setPinchZoom(false)

        volume_chart.setDrawGridBackground(false)

        val xAxis: XAxis = volume_chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false
        xAxis.granularity = 1f // only intervals of 1 day

        val leftAxis: YAxis = volume_chart.axisLeft
        leftAxis.setLabelCount(5, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis: YAxis = volume_chart.axisRight
        rightAxis.isEnabled = false

        val l: Legend = volume_chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f
    }

    private fun setupLineChart() {

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