package com.kvladislav.cryptowise.models.coin_cap.candles

import com.google.gson.annotations.SerializedName

data class CandleItem(

    @field:SerializedName("volume")
    val volume: Double? = null,

    @field:SerializedName("high")
    val high: Double? = null,

    @field:SerializedName("period")
    val period: Double? = null,

    @field:SerializedName("low")
    val low: Double? = null,

    @field:SerializedName("close")
    val close: Double? = null,

    @field:SerializedName("open")
    val open: Double? = null
)