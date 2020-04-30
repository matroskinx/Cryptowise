package com.kvladislav.cryptowise.services

import com.kvladislav.cryptowise.models.coin_cap.assets.CCAssetsResponse
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CCService {
    @GET("v2/assets")
    suspend fun getAssets(@Query("limit") limit: Int): CCAssetsResponse

    @GET("v2/candles")
    suspend fun getCandles(
        @Query("exchange") exchangeId: String = "binance",
        @Query("interval") interval: String,
        @Query("baseId") baseId: String,
        @Query("quoteId") quoteId: String,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): CandlesResponse
}