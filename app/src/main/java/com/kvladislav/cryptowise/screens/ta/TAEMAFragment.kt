package com.kvladislav.cryptowise.screens.ta

import android.graphics.Color
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.dialogs.BottomSheetFragment
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.FormatterUtils
import kotlinx.android.synthetic.main.fragment_ta_ema.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class TAEMAFragment : BaseFragment(R.layout.fragment_ta_ema) {
    override fun viewModel(): TAEMAViewModel = getViewModel {
        parametersOf(getSharedViewModel<AppViewModel>())
    }

    override fun setupView() {
        setupEMAChart()
        setupIntervalDropdown()
        setupTypeDropdown()
    }

    override fun setupListeners() {
        help_button.setOnClickListener {
            BottomSheetFragment(
                getString(R.string.ema_header),
                getString(R.string.ema_desc)
            ).show(parentFragmentManager, "TAG")
        }
    }


    override fun setupObservers() {
        viewModel().indicatorChartData.observe(viewLifecycleOwner) {
            fillEMAChartWithData(it)
        }
    }

    private fun fillEMAChartWithData(ema: List<Float>) {
        Timber.d("Displaying on chart: ${ema.count()}")
        val values: ArrayList<Entry> = ArrayList()
        var start = 1f
        for (v in ema) {
            values.add(Entry(start, v))
            start++
        }
        val dataSet = LineDataSet(values, getString(R.string.ema))
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        context?.run {
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.bright_blue))
            dataSet.color = ContextCompat.getColor(this, R.color.bright_blue)
        }
        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)

        ema_line_chart.data = lineData
        ema_line_chart.invalidate()
    }

    private fun setupEMAChart() {
        val rightAxis = ema_line_chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val xAxis = ema_line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        ema_line_chart.description.isEnabled = false
        ema_line_chart.setTouchEnabled(true)
        ema_line_chart.setDrawGridBackground(false)
        ema_line_chart.isDragEnabled = true
        ema_line_chart.setScaleEnabled(true)
        ema_line_chart.setPinchZoom(true)

        ema_line_chart.legend.isEnabled = false

        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val leftAxis = ema_line_chart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        ema_line_chart.legend.textColor = Color.WHITE

        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return FormatterUtils.format(value.toLong())
            }
        }
    }

    private fun setupIntervalDropdown() {
        val intervals = FrameInterval.values().map { it.friendlyName }
        context?.let {
            val adapter =
                ArrayAdapter(it, R.layout.dropdown_item, intervals)
            interval_dropdown.setAdapter(adapter)
        }
        interval_dropdown.setText(intervals[0], false)
        interval_dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                FrameInterval.HOUR_INTERVAL.position -> viewModel().onIntervalToggle(FrameInterval.HOUR_INTERVAL)
                FrameInterval.HOUR_8_INTERVAL.position -> viewModel().onIntervalToggle(FrameInterval.HOUR_8_INTERVAL)
                FrameInterval.DAY_INTERVAL.position -> viewModel().onIntervalToggle(FrameInterval.DAY_INTERVAL)
            }
        }
    }

    private fun setupTypeDropdown() {
        val types = EMAType.values().map {
            it.friendlyName
        }
        context?.let {
            val adapter =
                ArrayAdapter(it, R.layout.dropdown_item, types)
            type_dropdown.setAdapter(adapter)
        }
        type_dropdown.setText(types[0], false)
        type_dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                EMAType.EMA_5.position -> viewModel().onTypeToggle(EMAType.EMA_5)
                EMAType.EMA_20.position -> viewModel().onTypeToggle(EMAType.EMA_20)
                EMAType.EMA_50.position -> viewModel().onTypeToggle(EMAType.EMA_50)
                EMAType.EMA_100.position -> viewModel().onTypeToggle(EMAType.EMA_100)
            }
        }
    }

    enum class FrameInterval(val position: Int, val friendlyName: String) {
        HOUR_INTERVAL(0, "1 Hour"),
        HOUR_8_INTERVAL(1, "8 Hour"),
        DAY_INTERVAL(2, "1 Day"),
    }

    enum class EMAType(val position: Int, val friendlyName: String, val period: Int) {
        EMA_5(0, "EMA 5", 5),
        EMA_20(1, "EMA 20", 20),
        EMA_50(2, "EMA 50", 50),
        EMA_100(3, "EMA 100", 100),
    }
}