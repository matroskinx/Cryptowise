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
import kotlinx.android.synthetic.main.fragment_stochastic.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class StochasticFragment : BaseFragment(R.layout.fragment_stochastic) {
    override fun viewModel(): StochasticViewModel =
        getViewModel { parametersOf(getSharedViewModel<AppViewModel>()) }

    override fun setupView() {
        setupStochasticChart()
        setupIntervalDropdown()
        setupTypeDropdown()
    }

    override fun setupListeners() {
        help_button.setOnClickListener {
            BottomSheetFragment(
                getString(R.string.stochastic_header),
                getString(R.string.stochastic_desc)
            ).show(parentFragmentManager, "TAG")
        }
    }

    override fun setupObservers() {
        viewModel().indicatorChartData.observe(viewLifecycleOwner) {
            fillStochasticChartWithData(it)
        }
    }

    private fun fillStochasticChartWithData(stochastic: List<Float>) {
        Timber.d("Displaying on chart: ${stochastic.count()}")
        val values: ArrayList<Entry> = ArrayList()
        var start = 1f
        for (v in stochastic) {
            values.add(Entry(start, v))
            start++
        }
        val dataSet = LineDataSet(values, "Stochastic K%")
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        context?.run {
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.bright_blue))
            dataSet.color = ContextCompat.getColor(this, R.color.bright_blue)
        }
        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)

        stochastic_line_chart.data = lineData
        stochastic_line_chart.invalidate()
    }


    private fun setupStochasticChart() {
        val rightAxis = stochastic_line_chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val xAxis = stochastic_line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        stochastic_line_chart.description.isEnabled = false
        stochastic_line_chart.setTouchEnabled(true)
        stochastic_line_chart.setDrawGridBackground(false)
        stochastic_line_chart.isDragEnabled = true
        stochastic_line_chart.setScaleEnabled(true)
        stochastic_line_chart.setPinchZoom(true)

        stochastic_line_chart.legend.isEnabled = false

        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val leftAxis = stochastic_line_chart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        stochastic_line_chart.legend.textColor = Color.WHITE

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
        val types = StochasticType.values().map {
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
                StochasticType.ST_14.position -> viewModel().onTypeToggle(StochasticType.ST_14)
                StochasticType.ST_21.position -> viewModel().onTypeToggle(StochasticType.ST_21)
                StochasticType.ST_30.position -> viewModel().onTypeToggle(StochasticType.ST_30)
                StochasticType.ST_50.position -> viewModel().onTypeToggle(StochasticType.ST_50)
            }
        }
    }


    enum class FrameInterval(val position: Int, val friendlyName: String) {
        HOUR_INTERVAL(0, "1 Hour"),
        HOUR_8_INTERVAL(1, "8 Hour"),
        DAY_INTERVAL(2, "1 Day"),
    }

    enum class StochasticType(val position: Int, val friendlyName: String, val period: Int) {
        ST_14(0, "Stochastic 14", 14),
        ST_21(1, "Stochastic 21", 21),
        ST_30(2, "Stochastic 30", 30),
        ST_50(3, "Stochastic 50", 50),
    }
}