package com.kvladislav.cryptowise.models.coin_cap.candles

import com.google.gson.annotations.SerializedName

data class CandleItem(

    @field:SerializedName("volume")
    val volume: String? = null,

    @field:SerializedName("high")
    val high: String? = null,

    @field:SerializedName("period")
    val period: Long? = null,

    @field:SerializedName("low")
    val low: String? = null,

    @field:SerializedName("close")
    val close: String? = null,

    @field:SerializedName("open")
    val open: String? = null
)