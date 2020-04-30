package com.kvladislav.cryptowise.models.coin_cap.assets

data class CoinCapAssetItem(

    val symbol: String? = null,
    val volumeUsd24Hr: String? = null,
    val marketCapUsd: String? = null,
    val priceUsd: String? = null,
    val vwap24Hr: String? = null,
    val changePercent24Hr: String? = null,
    val name: String? = null,
    val rank: String? = null,
    val id: String? = null,
    val maxSupply: String? = null,
    val supply: String? = null
)
