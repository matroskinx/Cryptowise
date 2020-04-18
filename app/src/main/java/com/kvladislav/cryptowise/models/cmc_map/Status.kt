package com.kvladislav.cryptowise.models.cmc_map

import com.google.gson.annotations.SerializedName

data class Status(
	@SerializedName("error_message")
	val errorMessage: Any? = null,
	val elapsed: Int? = null,
	@SerializedName("credit_count")
	val creditCount: Int? = null,
	@SerializedName("error_code")
	val errorCode: Int? = null,
	val timestamp: String? = null
)
