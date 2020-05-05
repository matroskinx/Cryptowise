package com.kvladislav.cryptowise.screens.currency

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.kvladislav.cryptowise.models.coin_cap.ExchangeItem
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import com.kvladislav.cryptowise.models.coin_cap.markets.MarketsResponse
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.screens.transaction.AddFragment
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CurrencyDetailsViewModel(
    private val context: Context,
    val cmcData: CMCDataMinified
) : BaseViewModel(), KoinComponent {
    init {
        Timber.d("init: $cmcData")
    }

    private val coinCapRepository: CoinCapRepository by inject()
    private val dataStorage: DataStorage by inject()
    val timeInterval: MutableLiveData<TimeInterval> = MutableLiveData(TimeInterval.DAY)

    // data only for chart display
    val chartData: MutableLiveData<List<CandleItem>> = MutableLiveData()

    val hourData: MutableLiveData<List<CandleItem>> = MutableLiveData();
    val eightHourData: MutableLiveData<List<CandleItem>> = MutableLiveData();
    val dayData: MutableLiveData<List<CandleItem>> = MutableLiveData();


    fun requestCandles() {
        viewModelScope.launch {
            try {
                val exchangeId = loadDataFromBestMarket()
                    ?: throw IllegalStateException("Was unable to find market")
                Timber.d("From market: $exchangeId")

                val requestParams = getCoinCapIntervalParams(
                    timeInterval.value ?: throw IllegalStateException("Time interval is null")
                )
                Timber.d("request params: $requestParams")

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
    }

    private fun postCorrespondingData(interval: TimeInterval) {
        val count = TimeInterval.getCandleCount(interval)
        when (interval) {
            TimeInterval.DAY -> chartData.postValue(hourData.value?.take(count) ?: mutableListOf())
            TimeInterval.WEEK ->
                chartData.postValue(eightHourData.value?.take(count) ?: mutableListOf())
            TimeInterval.MONTH, TimeInterval.MONTH_3, TimeInterval.MONTH_6, TimeInterval.YEAR ->
                chartData.postValue(dayData.value?.take(count) ?: mutableListOf())
        }
    }

    private fun printCandlesDate(candles: List<CandleItem>) {
        val fmt = SimpleDateFormat("dd/MM/yyyy mm:ss aa")
        val formattedDate = fmt.format(candles.first().period!!)
        Timber.d(formattedDate)

        val fmt1 = SimpleDateFormat("dd/MM/yyyy mm:ss aa")
        val formattedDate1 = fmt.format(candles.last().period!!)
        Timber.d(formattedDate1)
    }


    private suspend fun loadDataFromBestMarket(): String? {
        val allMarkets = coinCapRepository.getExchanges()
        val coinMarkets: MarketsResponse =
            coinCapRepository.getTetherMarkets(cmcData.coinCapId).run {
                if (this.data == null || this.data.count() == 0) {
                    return@run coinCapRepository.getAllMarkets(cmcData.coinCapId)
                } else {
                    return@run this
                }
            }

        val marketRankMap = createMarketRankMap(allMarkets.data!!)
        var bestMarket: String? = null
        var currentRank = -1
        val marketNames: Set<String>? = coinMarkets.data?.map {
            it.exchangeId ?: ""
        }?.toSet()

        val intersection = marketNames?.intersect(DataStorage.TRUSTWORTHY_PROVIDERS)

        Timber.d("All markets: $marketRankMap")
        Timber.d("Available markets names: $marketNames")
        Timber.d("Intersection: $intersection")

        intersection?.forEach { market ->
            if (currentRank == -1 || currentRank > marketRankMap[market]!!) {
                currentRank = marketRankMap[market]!!
                bestMarket = market
            }
        }

        Timber.d("Picked best market: $bestMarket")

        return bestMarket
    }

    private fun createMarketRankMap(items: List<ExchangeItem>): HashMap<String, Int> {
        val map = HashMap<String, Int>()
        items.forEach {
            if (it.exchangeId != null && it.rank != null)
                map[it.exchangeId] = it.rank.toInt()
        }
        return map;
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
        val now = System.currentTimeMillis()
        return when (interval) {
            TimeInterval.DAY -> IntervalStore((now - DAY_INTERVAL), now, "h1")
            TimeInterval.WEEK -> IntervalStore((now - WEEK_INTERVAL), now, "h8")
            TimeInterval.MONTH -> IntervalStore((now - MONTH_INTERVAL), now, "d1")
            TimeInterval.MONTH_3 -> IntervalStore((now - MONTH_INTERVAL * 3), now, "d1")
            TimeInterval.MONTH_6 -> IntervalStore((now - MONTH_INTERVAL * 6), now, "d1")
            TimeInterval.YEAR -> IntervalStore((now - MONTH_INTERVAL * 12), now, "d1")
        }
    }

    fun onIntervalChange(interval: TimeInterval) {
        timeInterval.postValue(interval)
        postCorrespondingData(interval)
    }

    data class IntervalStore(val start: Long, val end: Long, val interval: String)

    enum class TimeInterval {
        DAY, WEEK, MONTH, MONTH_3, MONTH_6, YEAR;
        companion object {
            fun getCandleCount(interval: TimeInterval): Int {
                return when (interval) {
                    DAY -> 24
                    WEEK -> 21
                    MONTH -> 30
                    MONTH_3 -> 90
                    MONTH_6 -> 180
                    YEAR -> 365
                }
            }
        }
    }

    companion object {
        const val DAY_INTERVAL: Long = 1000 * 60 * 60 * 24
        const val WEEK_INTERVAL: Long = DAY_INTERVAL * 7
        const val MONTH_INTERVAL: Long = DAY_INTERVAL * 30
    }
}
