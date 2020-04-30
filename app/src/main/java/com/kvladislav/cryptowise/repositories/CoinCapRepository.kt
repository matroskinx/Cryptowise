package com.kvladislav.cryptowise.repositories

import com.google.gson.GsonBuilder
import com.kvladislav.cryptowise.models.coin_cap.assets.CCAssetsResponse
import com.kvladislav.cryptowise.models.coin_cap.candles.CandlesResponse
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

    suspend fun getCandles(assetId: String): CandlesResponse {
        // by default try to compare with tether
        return webservice.getCandles(interval = "h8", baseId = assetId, quoteId = "tether", start = 1586975561000, end = 1588271561000)
    }

    companion object {
        const val CC_BASE_URL = "https://api.coincap.io/"
    }

}