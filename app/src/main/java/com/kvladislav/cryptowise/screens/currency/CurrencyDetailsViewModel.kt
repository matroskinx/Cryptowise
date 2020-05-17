package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.enums.TAType
import com.kvladislav.cryptowise.enums.TimeInterval
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.screens.ta.TAMovingAverageFragment
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalStateException

class CurrencyDetailsViewModel(
    private val context: Context,
    val cmcData: CMCDataMinified
) : BaseViewModel(), KoinComponent {
    init {
        Timber.d("init: $cmcData")
    }

    private val coinCapRepository: CoinCapRepository by inject()
    val timeInterval: MutableLiveData<TimeInterval> = MutableLiveData(TimeInterval.DAY)

    // data only for chart display
    val chartData: MutableLiveData<List<CandleItem>> = MutableLiveData()

    private val hourData: MutableLiveData<List<CandleItem>> = MutableLiveData()
    private val eightHourData: MutableLiveData<List<CandleItem>> = MutableLiveData()
    private val dayData: MutableLiveData<List<CandleItem>> = MutableLiveData()

    val isLoaded = liveData(Dispatchers.IO) {
        emit(false)
        requestCandles()
        emit(true)
    }

    private suspend fun requestCandles() {
        try {
            val exchangeId = coinCapRepository.getBestRankedMarketForCoin(cmcData.coinCapId)
                ?: throw IllegalStateException("Was unable to find market")
            Timber.d("From market: $exchangeId")

            val hourCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "h1"
            )
            hourData.postValue(hourCandles.data ?: mutableListOf())
            val eightHourCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "h8"
            )
            eightHourData.postValue(eightHourCandles.data ?: mutableListOf())
            val dayCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "d1"
            )
            dayData.postValue(dayCandles.data ?: mutableListOf())

            timeInterval.value?.run {
                postCorrespondingData(interval = this)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getCurrentTimeFrameCandles(): List<CandleItem> {
        return timeInterval.value?.run {
            return@run when (this) {
                TimeInterval.DAY -> hourData.value
                TimeInterval.WEEK -> eightHourData.value
                TimeInterval.MONTH,
                TimeInterval.MONTH_3,
                TimeInterval.MONTH_6,
                TimeInterval.YEAR -> dayData.value
            }
        } ?: mutableListOf()
    }

    private fun postCorrespondingData(interval: TimeInterval) {
        val count = TimeInterval.getCandleCount(interval)
        when (interval) {
            TimeInterval.DAY -> chartData.postValue(
                hourData.value?.takeLast(count) ?: mutableListOf()
            )
            TimeInterval.WEEK ->
                chartData.postValue(eightHourData.value?.takeLast(count) ?: mutableListOf())
            TimeInterval.MONTH, TimeInterval.MONTH_3, TimeInterval.MONTH_6, TimeInterval.YEAR ->
                chartData.postValue(dayData.value?.takeLast(count) ?: mutableListOf())
        }
    }

    fun onAddTransactionTap() {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(AddFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, AddFragment.build(cmcData))
            }
        }
    }

    fun onIntervalChange(interval: TimeInterval) {
        timeInterval.postValue(interval)
        postCorrespondingData(interval)
    }

    fun onTATap(taType: TAType) {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(TAMovingAverageFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, TAMovingAverageFragment())
            }
        }
    }
}
