package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import com.kvladislav.cryptowise.models.coin_cap.markets.MarketItem
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalStateException

class CurrencyDetailsViewModel(
    private val context: Context,
    private val cmcData: CMCDataMinified
) : BaseViewModel(), KoinComponent {
    init {
        Timber.d("INIIIT: $cmcData")
    }

    private val coinCapRepository: CoinCapRepository by inject()
    val candlesData: MutableLiveData<CandlesResponse> = MutableLiveData()
    val timeInterval: MutableLiveData<TimeInterval> = MutableLiveData(TimeInterval.DAY)

    fun requestCandles() {
        viewModelScope.launch {
            try {
                val exchangeId = loadDataFromFirstAvailableMarket()
                    ?: throw IllegalStateException("Was unable to find market")
                Timber.d("From market: $exchangeId")

                val requestParams = getCoinCapIntervalParams(
                    timeInterval.value ?: throw IllegalStateException("Time interval is null")
                )


                Timber.d("RP: $requestParams")

                candlesData.postValue(
                    coinCapRepository.getCandles(
                        exchangeId = exchangeId,
                        baseId = cmcData.coinCapId,
                        interval = requestParams.interval,
                        start = requestParams.start,
                        end = requestParams.end
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }


    private suspend fun loadDataFromFirstAvailableMarket(): String? {
        var markets = coinCapRepository.getTetherMarkets(cmcData.coinCapId)
        if (markets.data?.count() == 0) {
            Timber.d("Fallback to all markets: ${cmcData.coinCapId}")
            markets = coinCapRepository.getAllMarkets(cmcData.coinCapId)
            Timber.d("$markets")
        }
        return markets.data?.let {
            findBestMarket(markets = it)?.exchangeId
        }
    }

    private fun findBestMarket(markets: List<MarketItem>): MarketItem? {
        var bestMarket: MarketItem? = markets[0]
        val bestRank: Int = markets[0].rank!!.toInt()
        for (market in markets) {
            if (market.rank != null && market.rank.toInt() < bestRank) {
                bestMarket = market
            }
        }
        return bestMarket
    }

    fun onAddTransactionTap() {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(AddFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, AddFragment.build(cmcData))
            }
        }
    }

    private fun getCoinCapIntervalParams(interval: TimeInterval): IntervalStore {
        val currentTime = System.currentTimeMillis()

        return when (interval) {
            TimeInterval.DAY -> IntervalStore(
                (currentTime - DAY_INTERVAL),
                currentTime,
                "h1"
            )
            TimeInterval.WEEK -> IntervalStore(
                (currentTime - WEEK_INTERVAL),
                currentTime,
                "h8"
            )
            TimeInterval.MONTH -> IntervalStore(
                (currentTime - MONTH_INTERVAL),
                currentTime,
                "d1"
            )
            TimeInterval.MONTH_3 -> IntervalStore(
                (currentTime - MONTH_INTERVAL * 3),
                currentTime,
                "d1"
            )
            TimeInterval.MONTH_6 -> IntervalStore(
                (currentTime - MONTH_INTERVAL * 6),
                currentTime,
                "d1"
            )
            TimeInterval.YEAR -> IntervalStore(
                (currentTime - MONTH_INTERVAL * 12),
                currentTime,
                "d1"
            )
        }
    }

    fun onIntervalChange(interval: TimeInterval) {
        timeInterval.postValue(interval)
        requestCandles()
    }

    data class IntervalStore(val start: Long, val end: Long, val interval: String)

    enum class TimeInterval {
        DAY, WEEK, MONTH, MONTH_3, MONTH_6, YEAR
    }

    companion object {
        const val DAY_INTERVAL: Long = 1000 * 60 * 60 * 24
        const val WEEK_INTERVAL: Long = DAY_INTERVAL * 7
        const val MONTH_INTERVAL: Long = DAY_INTERVAL * 30
    }
}
