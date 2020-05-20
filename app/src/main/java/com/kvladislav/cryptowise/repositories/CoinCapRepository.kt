package com.kvladislav.cryptowise.repositories

import com.google.gson.GsonBuilder
import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.models.coin_cap.ExchangeItem
import com.kvladislav.cryptowise.models.coin_cap.ExchangesResponse
import com.kvladislav.cryptowise.models.coin_cap.assets.CCAssetsResponse
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import com.kvladislav.cryptowise.models.coin_cap.markets.MarketsResponse
import com.kvladislav.cryptowise.services.CoinCapService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class CoinCapRepository {

    private val webservice: CoinCapService by lazy {
        Retrofit.Builder()
            .baseUrl(CC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(CoinCapService::class.java)
    }

    suspend fun getAssets(): CCAssetsResponse {
        return webservice.getAssets(2000)
    }

    suspend fun getCandles(
        exchangeId: String,
        baseId: String,
        quoteId: String = DEFAULT_QUOTE_ID,
        interval: String,
        start: Long? = null,
        end: Long? = null
    ): CandlesResponse {
        return webservice.getCandles(
            exchangeId = exchangeId,
            interval = interval,
            baseId = baseId,
            quoteId = quoteId,
            start = start,
            end = end
        )
    }

    suspend fun getMarketsByExchangeId(
        exchangeId: String
    ): MarketsResponse {
        return webservice.getMarkets(quoteId = DEFAULT_QUOTE_ID, exchangeId = exchangeId)
    }

    suspend fun getTetherMarkets(
        baseId: String,
        quoteId: String = DEFAULT_QUOTE_ID
    ): MarketsResponse {
        return webservice.getMarkets(baseId, quoteId)
    }

    suspend fun getAllMarkets(
        baseId: String
    ): MarketsResponse {
        return webservice.getMarkets(baseId = baseId)
    }

    suspend fun getExchanges(): ExchangesResponse {
        return webservice.getExchanges()
    }

    companion object {
        const val DEFAULT_QUOTE_ID = "tether"
        const val CC_BASE_URL = "https://api.coincap.io/"
    }

    /// return best market id for given coin
    suspend fun getBestRankedMarketForCoin(coinCapCoinId: String): String? {
        val allMarkets = getExchanges()
        val coinMarkets: MarketsResponse =
            getTetherMarkets(coinCapCoinId).run {
                if (this.data == null || this.data.count() == 0) {
                    return@run getAllMarkets(coinCapCoinId)
                } else {
                    return@run this
                }
            }

        val marketRankMap = createMarketRankMap(allMarkets.data!!)
        var bestMarket: String? = null
        var currentRank = -1
        //marketNames = getNameSet(coinMarkets)
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

}