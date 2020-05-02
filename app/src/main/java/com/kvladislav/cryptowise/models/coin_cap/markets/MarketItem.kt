package com.kvladislav.cryptowise.models.coin_cap.markets

import com.google.gson.annotations.SerializedName

data class MarketItem(

    @field:SerializedName("exchangeId")
    val exchangeId: String? = null,

    @field:SerializedName("volumeUsd24Hr")
    val volumeUsd24Hr: String? = null,

    @field:SerializedName("priceUsd")
    val priceUsd: String? = null,

    @field:SerializedName("priceQuote")
    val priceQuote: String? = null,

    @field:SerializedName("percentExchangeVolume")
    val percentExchangeVolume: Any? = null,

    @field:SerializedName("rank")
    val rank: String? = null,

    @field:SerializedName("tradesCount24Hr")
    val tradesCount24Hr: Any? = null,

    @field:SerializedName("baseId")
    val baseId: String? = null,

    @field:SerializedName("updated")
    val updated: Long? = null,

    @field:SerializedName("quoteId")
    val quoteId: String? = null,

    @field:SerializedName("baseSymbol")
    val baseSymbol: String? = null,

    @field:SerializedName("quoteSymbol")
    val quoteSymbol: String? = null
)