package com.kvladislav.cryptowise.services

import com.kvladislav.cryptowise.models.coin_cap.CCAssetsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CCService {
    @GET("v2/assets")
    suspend fun getAssets(@Query("limit") limit: Int): CCAssetsResponse
}