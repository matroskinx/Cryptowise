package com.kvladislav.cryptowise.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class MyValueFormatter(private val suffix: String) :
    ValueFormatter() {
    private val mFormat: DecimalFormat = DecimalFormat("###,###,###,##0.0")
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value).toString() + suffix
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return if (axis is XAxis) {
            mFormat.format(value)
        } else if (value > 0) {
            mFormat.format(value).toString() + suffix
        } else {
            mFormat.format(value)
        }
    }
}