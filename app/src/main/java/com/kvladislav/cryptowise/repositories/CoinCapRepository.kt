package com.kvladislav.cryptowise.repositories

import com.google.gson.GsonBuilder
import com.kvladislav.cryptowise.models.coin_cap.assets.CCAssetsResponse
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import com.kvladislav.cryptowise.models.coin_cap.markets.MarketsResponse
import com.kvladislav.cryptowise.services.CCService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CoinCapRepository {

    private val webservice: CCService by lazy {
        Retrofit.Builder()
            .baseUrl(CC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(CCService::class.java)
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

    companion object {
        const val DEFAULT_QUOTE_ID = "tether"
        const val CC_BASE_URL = "https://api.coincap.io/"
    }

}