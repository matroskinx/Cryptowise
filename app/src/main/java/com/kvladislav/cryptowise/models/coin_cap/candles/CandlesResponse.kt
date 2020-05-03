package com.kvladislav.cryptowise.models.coin_cap.candles

import com.google.gson.annotations.SerializedName

data class CandlesResponse(

	@field:SerializedName("data")
	val data: List<CandleItem>? = null,

	@field:SerializedName("timestamp")
	val timestamp: Long? = null
)