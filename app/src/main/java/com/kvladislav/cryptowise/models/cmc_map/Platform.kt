package com.kvladislav.cryptowise.models.cmc_map

import com.google.gson.annotations.SerializedName

data class Platform(
	val symbol: String? = null,
	val name: String? = null,
	@SerializedName("token_address")
	val tokenAddress: String? = null,
	val id: Int? = null,
	val slug: String? = null
)
