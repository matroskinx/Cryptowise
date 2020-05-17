package com.kvladislav.cryptowise.screens.currency

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.enums.TAType
import com.kvladislav.cryptowise.enums.TimeInterval
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.utils.FormatterUtils
import com.kvladislav.cryptowise.utils.TAUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_currency_details.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class CurrencyDetailsFragment : BaseFragment(R.layout.fragment_currency_details) {
    override fun viewModel(): CurrencyDetailsViewModel =
        getViewModel { parametersOf(parseArguments(arguments)) }

    override fun setupView() {
        setupChart()
        setupVolumeChart()
        setupLineChart()
        day_chip.isChecked = true

        Picasso.get()
            .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${viewModel().cmcData.cmcId}.png")
            .into(crypto_iv)
        val str = "${viewModel().cmcData.symbol}/USDT"
        crypto_tv.text = str
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

        ta_button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pick TA")
                .setItems(
                    TAType.values().map {
                        it.friendlyName
                    }.toTypedArray()
                ) { _, which ->
                    val taType = when (which) {
                        TAType.SIMPLE_MOVING_AVERAGE.position -> TAType.SIMPLE_MOVING_AVERAGE
                        TAType.EXP_MOVING_AVERAGE.position -> TAType.EXP_MOVING_AVERAGE
                        TAType.OSCILLATOR.position -> TAType.OSCILLATOR
                        else -> throw IllegalArgumentException("There is no dialog value for $which")
                    }

                    viewModel().onTATap(taType)

                }.show()
        }

        chip_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.day_chip -> TimeInterval.DAY
                R.id.week_chip -> TimeInterval.WEEK
                R.id.month_chip -> TimeInterval.MONTH
                R.id.month_3_chip -> TimeInterval.MONTH_3
                R.id.month_6_chip -> TimeInterval.MONTH_6
                R.id.year_chip -> TimeInterval.YEAR
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
            viewModel().timeInterval.value?.run {
                val allCandles = viewModel().getCurrentTimeFrameCandles()
                val dataPointCount = TimeInterval.getCandleCount(this)
                buildSMA(allCandles, dataPointCount)
                buildEMA(allCandles)
            }
        }

        viewModel().isLoaded.observe(viewLifecycleOwner) { isLoaded ->
            if (isLoaded) {
                main_ll.visibility = View.VISIBLE
                loading_layout.visibility = View.GONE
            } else {
                main_ll.visibility = View.GONE
                loading_layout.visibility = View.VISIBLE
            }
        }
    }


    private fun buildSMA(allCandles: List<CandleItem>, dataPointCount: Int) {
        val periods = mutableListOf(5, 10, 20, 30, 50, 100)
        val views = mutableListOf(sma_5, sma_10, sma_20, sma_30, sma_50, sma_100)

        val fillPeriodIdx = 0

        for (i in 0 until periods.count()) {
            val periodCandles = allCandles.takeLast(dataPointCount + periods[i] - 1)
            val sma = TAUtils.simpleMovingAverage(periodCandles, periods[i])
            if (i == fillPeriodIdx) {
                displaySimpleMovingAverage(sma)
            }
            views[i].text = sma.last().toString()
        }
    }

    private fun buildEMA(allCandles: List<CandleItem>) {
        val periods = mutableListOf(10, 20)
        val views = mutableListOf(ema_12, ema_26)

        for (i in 0 until periods.count()) {
            val emas = calculateEMA(allCandles, periods[i])
            views[i].text = emas.last().toString()
            displaySimpleMovingAverage(emas)
        }
    }

    private fun calculateEMA(allCandles: List<CandleItem>, mockPeriod: Int): MutableList<Float> {
        val periodCandles = allCandles.takeLast(mockPeriod * 2)
        val smaCandles = periodCandles.take(mockPeriod)
        val emaCandles = periodCandles.takeLast(mockPeriod)
        val initialSMA = TAUtils.candlesAverage(smaCandles)

        val smoothingConstant: Float = 2f / (mockPeriod + 1)

        val EMAs = mutableListOf<Float>()
        for (i in 0 until emaCandles.count()) {
            val previousDay = if (i == 0) initialSMA else (EMAs[i - 1])
            val value =
                (emaCandles[i].close!!.toFloat() - previousDay) * smoothingConstant + previousDay
            EMAs.add(value)
        }

        return EMAs
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
        dataSet.decreasingColor = ContextCompat.getColor(requireContext(), R.color.red_600)
        dataSet.decreasingPaintStyle = Paint.Style.FILL_AND_STROKE
        dataSet.increasingColor = ContextCompat.getColor(requireContext(), R.color.green_600)
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
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.bright_blue)
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)
        val data = BarData(dataSet)
        data.setValueTextSize(8f)
        data.barWidth = 0.7f
        volume_chart.data = data
        volume_chart.invalidate()
    }

    private fun displaySimpleMovingAverage(sma: List<Float>) {
        Timber.d("Displaying on chart: ${sma.count()}")
        val values: ArrayList<Entry> = ArrayList()
        var start = 1f
        for (v in sma) {
            values.add(Entry(start, v))
            start++
        }
        val dataSet = LineDataSet(values, "SMA 5")
        context?.run {
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.bright_blue))
            dataSet.color = ContextCompat.getColor(this, R.color.bright_blue)
        }
        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)

        line_chart.data = lineData
        line_chart.invalidate()
    }

    private fun setupChart() {
        val candleStickChart: CandleStickChart = ohlcv_chart
        candleStickChart.description.isEnabled = false
        candleStickChart.setPinchZoom(false)
        candleStickChart.setDrawGridBackground(false)
        val xAxis = candleStickChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        val leftAxis = candleStickChart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        leftAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_slab)

        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return FormatterUtils.format(value.toLong())
            }
        }

        val rightAxis = candleStickChart.axisRight
        rightAxis.isEnabled = false

        candleStickChart.legend.textColor = Color.WHITE
        candleStickChart.legend.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.roboto_slab)
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
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        leftAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_slab)

        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return FormatterUtils.format(value.toLong())
            }
        }

        val rightAxis: YAxis = volume_chart.axisRight
        rightAxis.isEnabled = false

        volume_chart.legend.textColor = Color.WHITE
        volume_chart.legend.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_slab)
    }

    private fun setupLineChart() {
        val rightAxis = line_chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val xAxis = line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        line_chart.description.isEnabled = false
        line_chart.setTouchEnabled(true)
        line_chart.setDrawGridBackground(false)
        line_chart.isDragEnabled = true
        line_chart.setScaleEnabled(true)
        line_chart.setPinchZoom(true)

        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val leftAxis = line_chart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        line_chart.legend.textColor = Color.WHITE

        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return FormatterUtils.format(value.toLong())
            }
        }
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