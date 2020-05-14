package com.kvladislav.cryptowise.services

import com.kvladislav.cryptowise.models.coin_cap.ExchangesResponse
import com.kvladislav.cryptowise.models.coin_cap.assets.CCAssetsResponse
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import com.kvladislav.cryptowise.models.coin_cap.markets.MarketsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinCapService {
    @GET("v2/assets")
    suspend fun getAssets(
        @Query("limit") limit: Int
    ): CCAssetsResponse

    @GET("v2/candles")
    suspend fun getCandles(
        @Query("exchange") exchangeId: String,
        @Query("interval") interval: String,
        @Query("baseId") baseId: String,
        @Query("quoteId") quoteId: String,
        @Query("start") start: Long? = null,
        @Query("end") end: Long? = null
    ): CandlesResponse

    @GET("v2/markets")
    suspend fun getMarkets(
        @Query("exchangeId") exchangeId: String? = null,
        @Query("baseId") baseId: String? = null,
        @Query("quoteId") quoteId: String? = null,
        @Query("limit") limit: Int = 2000
    ): MarketsResponse

    @GET("v2/exchanges")
    suspend fun getExchanges(
        @Query("limit") limit: Int = 1000
    ): ExchangesResponse
}