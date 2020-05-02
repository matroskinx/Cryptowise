package com.kvladislav.cryptowise.models.coin_cap

import com.google.gson.annotations.SerializedName

data class ExchangeItem(

    @field:SerializedName("exchangeId")
    val exchangeId: String? = null,

    @field:SerializedName("percentTotalVolume")
    val percentTotalVolume: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("volumeUsd")
    val volumeUsd: String? = null,

    @field:SerializedName("exchangeUrl")
    val exchangeUrl: String? = null,

    @field:SerializedName("rank")
    val rank: String? = null,

    @field:SerializedName("socket")
    val socket: Boolean? = null,

    @field:SerializedName("updated")
    val updated: Long? = null,

    @field:SerializedName("tradingPairs")
    val tradingPairs: String? = null
)