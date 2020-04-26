package com.kvladislav.cryptowise.models.cmc_listings

data class CMCListingsResponse(
    val data: List<ListingItem>? = null,
    val status: Status? = null
)
