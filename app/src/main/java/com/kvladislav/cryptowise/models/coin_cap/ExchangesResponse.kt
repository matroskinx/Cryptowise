package com.kvladislav.cryptowise.models.coin_cap

import com.google.gson.annotations.SerializedName

data class ExchangesResponse(

	@field:SerializedName("data")
	val data: List<ExchangeItem>? = null,

	@field:SerializedName("timestamp")
	val timestamp: Long? = null
)