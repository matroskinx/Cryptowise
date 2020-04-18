package com.kvladislav.cryptowise.services

import com.kvladislav.cryptowise.models.cmc_map.CMCIDMapResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OverviewService {
    @GET("cryptocurrency/map")
    suspend fun getIDMap(@Header("X-CMC_PRO_API_KEY") apiKey: String): CMCIDMapResponse
}