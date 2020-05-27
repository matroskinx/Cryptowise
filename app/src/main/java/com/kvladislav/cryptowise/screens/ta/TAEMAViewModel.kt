package com.kvladislav.cryptowise.screens.ta

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.TAUtils

class TAEMAViewModel(
    private val context: Context,
    private val appViewModel: AppViewModel
) : BaseViewModel() {

    val indicatorChartData = MutableLiveData<List<Float>>(mutableListOf())

    private val frameInterval = MutableLiveData(TAEMAFragment.FrameInterval.HOUR_INTERVAL)
    private val emaType = MutableLiveData(TAEMAFragment.EMAType.EMA_5)

    init {
        buildEMA(frameInterval.value!!, emaType.value!!)
    }

    fun onIntervalToggle(frameInterval: TAEMAFragment.FrameInterval) {
        this.frameInterval.postValue(frameInterval)
        emaType.value?.run {
            buildEMA(frameInterval, this)
        }
    }

    fun onTypeToggle(emaType: TAEMAFragment.EMAType) {
        this.emaType.postValue(emaType)
        frameInterval.value?.run {
            buildEMA(this, emaType)
        }
    }

    private fun buildEMA(
        frameInterval: TAEMAFragment.FrameInterval,
        emaType: TAEMAFragment.EMAType
    ) {
        val allCandles = when (frameInterval) {
            TAEMAFragment.FrameInterval.HOUR_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH1
            TAEMAFragment.FrameInterval.HOUR_8_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH8
            TAEMAFragment.FrameInterval.DAY_INTERVAL -> appViewModel.candlePeriodicData.value?.dataD1
        } ?: listOf()
        val period = emaType.period
        indicatorChartData.postValue(calculateEMA(allCandles, period))
    }

    /// return exponential moving average for given period, for example EMA(20) for interval with
    /// length of 20
    private fun calculateEMA(allCandles: List<CandleItem>, period: Int): List<Float> {
        val periodCandles = allCandles.takeLast(period * 2)
        val smaCandles = periodCandles.take(period)
        val emaCandles = periodCandles.takeLast(period)
        val initialSMA = TAUtils.candlesAverage(smaCandles)

        val smoothingConstant: Float = 2f / (period + 1)

        val EMAs = mutableListOf<Float>()
        for (i in 0 until emaCandles.count()) {
            val previousDay = if (i == 0) initialSMA else (EMAs[i - 1])
            val value =
                (emaCandles[i].close!!.toFloat() - previousDay) * smoothingConstant + previousDay
            EMAs.add(value)
        }

        return EMAs
    }
}