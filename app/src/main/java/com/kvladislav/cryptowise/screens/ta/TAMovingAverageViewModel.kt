package com.kvladislav.cryptowise.screens.ta

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.TAUtils
import java.lang.IllegalArgumentException

class TAMovingAverageViewModel(
    private val context: Context,
    private val appViewModel: AppViewModel
) : BaseViewModel() {

    val indicatorChartData = MutableLiveData<List<Float>>(mutableListOf())

    // interval 1h, 8h, 1d
    // type 5, 20, 50, 100
    fun onIntervalToggle(intervalToggleId: Int, typeToggleId: Int) {
        buildSMA(intervalToggleId, typeToggleId)
    }

    private fun buildSMA(intervalToggleId: Int, typeToggleId: Int) {
        val candles = when (intervalToggleId) {
            TAMovingAverageFragment.HOUR_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH1
            TAMovingAverageFragment.HOUR_8_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH8
            TAMovingAverageFragment.DAY_INTERVAL -> appViewModel.candlePeriodicData.value?.dataD1
            else -> throw IllegalArgumentException("No candles for given id $intervalToggleId")
        } ?: mutableListOf()

        val fillPeriodIdx = typeToggleId - 1

        for (i in 0 until periods.count()) {
            val periodCandles = candles.takeLast(dataPointCount + periods[i] - 1)
            val sma = TAUtils.simpleMovingAverage(periodCandles, periods[i])
            if (i == fillPeriodIdx) {
                indicatorChartData.postValue(sma)
            }
        }
    }

    companion object {
        const val dataPointCount = 100
        val periods = listOf(5, 10, 20, 30, 50, 100)
    }
}