package com.kvladislav.cryptowise.models.cmc_map

import com.google.gson.annotations.SerializedName

data class DataItem(
    val symbol: String? = null,
    @SerializedName("is_active")
    val isActive: Int? = null,
    @SerializedName("last_historical_data")
    val lastHistoricalData: String? = null,
    val name: String? = null,
    val rank: Int? = null,
    val id: Int? = null,
    val slug: String? = null,
    val platform: Platform? = null,
    @SerializedName("first_historical_data")
    val firstHistoricalData: String? = null
)
