package com.kvladislav.cryptowise.screens.ta

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.dialogs.BottomSheetFragment
import com.kvladislav.cryptowise.extensions.formatDigits
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.FormatterUtils
import kotlinx.android.synthetic.main.fragment_ta_moving_average.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.lang.Exception

class TAMovingAverageFragment : BaseFragment(R.layout.fragment_ta_moving_average) {
    override fun viewModel(): TAMovingAverageViewModel = getViewModel {
        parametersOf(getSharedViewModel<AppViewModel>())
    }

    override fun setupView() {
        setupSMAChart()
        interval_toggle_group.check(R.id.hour_btn)
        sma_type_group.check(R.id.sma_5_btn)
        toggleChartChange()
    }

    override fun setupListeners() {
        help_button.setOnClickListener {
            BottomSheetFragment(
                getString(R.string.sma_header),
                getString(R.string.sma_desc)
            ).show(parentFragmentManager, "TAG")
        }

        interval_toggle_group.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                toggleChartChange()
            }
            Timber.d("MBG: $checkedId $isChecked")
        }

        sma_type_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                toggleChartChange()
                Timber.d("fff: $checkedId $isChecked")
            }
        }
    }

    private fun toggleChartChange() {
        val intervalId = interval_toggle_group.checkedButtonId
        val typeId = sma_type_group.checkedButtonId
        viewModel().onIntervalToggle(mapIntervalToId(intervalId), mapTypeToId(typeId))
    }

    private fun mapTypeToId(checkedId: Int) = when (checkedId) {
        R.id.sma_5_btn -> SMA_5
        R.id.sma_20_btn -> SMA_20
        R.id.sma_50_btn -> SMA_50
        R.id.sma_100_btn -> SMA_100
        else -> throw Exception("Wrong button $checkedId")
    }

    private fun mapIntervalToId(checkedId: Int) = when (checkedId) {
        R.id.hour_btn -> HOUR_INTERVAL
        R.id.hour8_btn -> HOUR_8_INTERVAL
        R.id.day_btn -> DAY_INTERVAL
        else -> throw Exception("Wrong button $checkedId")
    }


    override fun setupObservers() {
        viewModel().indicatorChartData.observe(viewLifecycleOwner) { smaData ->
            fillSMAChartWithData(smaData)
        }
    }

    private fun fillSMAChartWithData(sma: List<Float>) {
        Timber.d("Displaying on chart: ${sma.count()}")
        val values: ArrayList<Entry> = ArrayList()
        var start = 1f
        for (v in sma) {
            values.add(Entry(start, v))
            start++
        }
        val dataSet = LineDataSet(values, "SMA 5")
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        context?.run {
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.bright_blue))
            dataSet.color = ContextCompat.getColor(this, R.color.bright_blue)
        }
        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)

        sma_line_chart.data = lineData
        sma_line_chart.invalidate()
    }

    private fun setupSMAChart() {
        val rightAxis = sma_line_chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        val xAxis = sma_line_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.isEnabled = false

        sma_line_chart.description.isEnabled = false
        sma_line_chart.setTouchEnabled(true)
        sma_line_chart.setDrawGridBackground(false)
        sma_line_chart.isDragEnabled = true
        sma_line_chart.setScaleEnabled(true)
        sma_line_chart.setPinchZoom(true)

        sma_line_chart.legend.isEnabled = false

        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val leftAxis = sma_line_chart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.textColor = Color.WHITE
        sma_line_chart.legend.textColor = Color.WHITE

        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (value < 1000) {
                    value.toDouble().formatDigits(2)
                } else FormatterUtils.coolFormat(value.toDouble())
            }
        }
    }

    companion object {
        const val HOUR_INTERVAL = 1
        const val HOUR_8_INTERVAL = 2
        const val DAY_INTERVAL = 3

        const val SMA_5 = 1
        const val SMA_20 = 2
        const val SMA_50 = 3
        const val SMA_100 = 4
    }
}