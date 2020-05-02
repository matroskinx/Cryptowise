package com.kvladislav.cryptowise.models.coin_cap.markets

import com.google.gson.annotations.SerializedName

data class MarketsResponse(

    @field:SerializedName("data")
    val data: List<MarketItem>? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null
)