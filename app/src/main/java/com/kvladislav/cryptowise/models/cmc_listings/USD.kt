package com.kvladislav.cryptowise.models.cmc_listings

import com.google.gson.annotations.SerializedName

data class USD(
    @SerializedName("percent_change_1h")
    val percentChange1h: Double? = null,
    @SerializedName("last_updated")
    val lastUpdated: String? = null,
    @SerializedName("percent_change_24h")
    val percentChange24h: Double? = null,
    @SerializedName("market_cap")
    val marketCap: Double? = null,
    val price: Double? = null,
    @SerializedName("volume_24h")
    val volume24h: Double? = null,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double? = null
)
