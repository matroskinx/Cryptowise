package com.kvladislav.cryptowise.repositories

import com.google.gson.GsonBuilder
import com.kvladislav.cryptowise.models.coin_cap.CCAssetsResponse
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

    companion object {
        const val CC_BASE_URL = "https://api.coincap.io/"
    }

}