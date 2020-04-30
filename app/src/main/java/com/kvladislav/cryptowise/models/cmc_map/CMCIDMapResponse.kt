package com.kvladislav.cryptowise.models.cmc_map

data class CMCIDMapResponse(
	val data: List<CMCMapItem>? = null,
	val status: Status? = null
)
