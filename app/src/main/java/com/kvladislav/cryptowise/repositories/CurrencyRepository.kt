package com.kvladislav.cryptowise.repositories

import com.google.gson.GsonBuilder
import com.kvladislav.cryptowise.models.cmc_listings.CMCListingsResponse
import com.kvladislav.cryptowise.models.cmc_map.CMCIDMapResponse
import com.kvladislav.cryptowise.services.CurrencyService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyRepository {

    private val webservice: CurrencyService by lazy {
        Retrofit.Builder()
            .baseUrl(CMC_MAP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(CurrencyService::class.java)
    }

    suspend fun getIDMap(): CMCIDMapResponse {
        return webservice.getIDMap(CMC_API_KEY)
    }

    suspend fun getListings() : CMCListingsResponse {
        return webservice.getLatestListings(CMC_API_KEY)
    }

    companion object {
        const val CMC_MAP_BASE_URL = "https://sandbox-api.coinmarketcap.com/v1/"
        const val CMC_API_KEY = "24602dd9-87c5-4ede-80ef-f78f8dfb74af"
    }

}