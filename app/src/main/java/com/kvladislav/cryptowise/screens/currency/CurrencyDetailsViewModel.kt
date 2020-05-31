package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.base.SingleLiveEvent
import com.kvladislav.cryptowise.enums.TAType
import com.kvladislav.cryptowise.enums.TimeInterval
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.CandlePeriodicData
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.screens.ta.StochasticFragment
import com.kvladislav.cryptowise.screens.ta.TAEMAFragment
import com.kvladislav.cryptowise.screens.ta.TAMovingAverageFragment
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import com.kvladislav.cryptowise.utils.handleException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalStateException

class CurrencyDetailsViewModel(
    private val context: Context,
    private val appViewModel: AppViewModel,
    val cmcData: CMCDataMinified
) : BaseViewModel(), KoinComponent {
    private val coinCapRepository: CoinCapRepository by inject()
    val timeInterval: MutableLiveData<TimeInterval> = MutableLiveData(TimeInterval.DAY)
    val connectionErrorLiveData = SingleLiveEvent<String>()
    val assetModel = MutableLiveData<CombinedAssetModel>()

    // data only for chart display
    val chartData: MutableLiveData<List<CandleItem>> = MutableLiveData()

    init {
        fillCombinedAssetModel()
        requestCandles()
    }

    private fun requestCandles() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val exchangeId = coinCapRepository.getBestRankedMarketForCoin(cmcData.coinCapId)
                ?: throw IllegalStateException("Was unable to find market")
            Timber.d("From market: $exchangeId")

            val hourCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "h1"
            ).data ?: mutableListOf()
            val eightHourCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "h8"
            ).data ?: mutableListOf()
            val dayCandles = coinCapRepository.getCandles(
                exchangeId = exchangeId,
                baseId = cmcData.coinCapId,
                interval = "d1"
            ).data ?: mutableListOf()
            connectionErrorLiveData.postValue(null)
            val candlePeriodicData = CandlePeriodicData(
                cmcData.cmcId,
                hourCandles,
                eightHourCandles,
                dayCandles
            )
            appViewModel.candlePeriodicData.postValue(candlePeriodicData)
            timeInterval.value?.run { postCorrespondingData(this, candlePeriodicData) }
        } catch (e: Exception) {
            Timber.e(e)
            onException(e)
        }
    }

    private fun fillCombinedAssetModel() {
        appViewModel.assetListings.value?.find { it.cmcId == cmcData.cmcId }?.run {
            assetModel.postValue(this)
        }
    }

    private fun onException(e: Exception) {
        connectionErrorLiveData.postValue(context.getString(handleException(e)))
    }

    private fun postCorrespondingData(
        interval: TimeInterval,
        candlePeriodicData: CandlePeriodicData?
    ) {
        val count = TimeInterval.getCandleCount(interval)
        val data: CandlePeriodicData? = candlePeriodicData ?: appViewModel.candlePeriodicData.value
        when (interval) {
            TimeInterval.DAY -> chartData.postValue(
                data?.dataH1?.takeLast(count) ?: mutableListOf()
            )
            TimeInterval.WEEK ->
                chartData.postValue(
                    data?.dataH8?.takeLast(count)
                        ?: mutableListOf()
                )
            TimeInterval.MONTH, TimeInterval.MONTH_3, TimeInterval.MONTH_6, TimeInterval.YEAR ->
                chartData.postValue(
                    data?.dataD1?.takeLast(count)
                        ?: mutableListOf()
                )
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
        postCorrespondingData(interval, null)
    }

    fun onTATap(taType: TAType) {
        val fragment = when (taType) {
            TAType.SIMPLE_MOVING_AVERAGE -> TAMovingAverageFragment()
            TAType.EXP_MOVING_AVERAGE -> TAEMAFragment()
            TAType.OSCILLATOR -> StochasticFragment()
        }

        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(fragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, fragment)
            }
        }
    }

    fun onRefreshTap() {
        requestCandles()
    }
}
