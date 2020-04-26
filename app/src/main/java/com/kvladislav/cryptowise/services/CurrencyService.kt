package com.kvladislav.cryptowise.services

import com.kvladislav.cryptowise.models.cmc_listings.CMCListingsResponse
import com.kvladislav.cryptowise.models.cmc_map.CMCIDMapResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CurrencyService {
    @GET("cryptocurrency/map")
    suspend fun getIDMap(@Header("X-CMC_PRO_API_KEY") apiKey: String): CMCIDMapResponse

    @GET("cryptocurrency/listings/latest")
    suspend fun getLatestListings(
        @Header("X-CMC_PRO_API_KEY") apiKey: String,
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 100
    ): CMCListingsResponse
}