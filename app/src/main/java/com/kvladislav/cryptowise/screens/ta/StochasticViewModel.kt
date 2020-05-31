package com.kvladislav.cryptowise.screens.ta

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.TAUtils

class StochasticViewModel(
    private val context: Context,
    private val appViewModel: AppViewModel
) : BaseViewModel() {

    val indicatorChartData = MutableLiveData<List<Float>>(mutableListOf())

    private val frameInterval = MutableLiveData(StochasticFragment.FrameInterval.HOUR_INTERVAL)
    private val emaType = MutableLiveData(StochasticFragment.StochasticType.ST_14)


    init {
        buildStochastic(frameInterval.value!!, emaType.value!!)
    }

    fun onIntervalToggle(frameInterval: StochasticFragment.FrameInterval) {
        this.frameInterval.postValue(frameInterval)
        emaType.value?.run {
            buildStochastic(frameInterval, this)
        }
    }

    fun onTypeToggle(emaType: StochasticFragment.StochasticType) {
        this.emaType.postValue(emaType)
        frameInterval.value?.run {
            buildStochastic(this, emaType)
        }
    }

    private fun buildStochastic(
        frameInterval: StochasticFragment.FrameInterval,
        emaType: StochasticFragment.StochasticType
    ) {
        val allCandles = when (frameInterval) {
            StochasticFragment.FrameInterval.HOUR_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH1
            StochasticFragment.FrameInterval.HOUR_8_INTERVAL -> appViewModel.candlePeriodicData.value?.dataH8
            StochasticFragment.FrameInterval.DAY_INTERVAL -> appViewModel.candlePeriodicData.value?.dataD1
        } ?: listOf()
        val period = emaType.period
        indicatorChartData.postValue(calculateStochastic(allCandles, period))
    }

    private fun calculateStochastic(allCandles: List<CandleItem>, period: Int): List<Float> {
        val defaultDataPointCount = 100
        val periodicCandles = allCandles.takeLast(defaultDataPointCount + period)
        return TAUtils.stochasticOscillator(periodicCandles, period)
    }

}