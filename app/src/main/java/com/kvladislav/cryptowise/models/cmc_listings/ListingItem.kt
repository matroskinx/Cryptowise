package com.kvladislav.cryptowise.models.cmc_listings

import com.google.gson.annotations.SerializedName

data class ListingItem(
    val symbol: String? = null,

    @SerializedName("circulating_supply")
    val circulatingSupply: Float? = null,

    @SerializedName("last_updated")
    val lastUpdated: String? = null,

    @SerializedName("total_supply")
    val totalSupply: Float? = null,

    @SerializedName("cmc_rank")
    val cmcRank: Int? = null,
    val platform: Platform? = null,
    val tags: List<String?>? = null,

    @SerializedName("date_added")
    val dateAdded: String? = null,
    val quote: Quote? = null,

    @SerializedName("num_market_pairs")
    val numMarketPairs: Int? = null,
    val name: String? = null,

    @SerializedName("max_supply")
    val maxSupply: Float? = null,
    val id: Int? = null,
    val slug: String? = null
)
