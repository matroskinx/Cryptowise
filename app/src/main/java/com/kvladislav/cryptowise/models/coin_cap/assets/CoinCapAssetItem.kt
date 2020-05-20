package com.kvladislav.cryptowise.models.coin_cap.assets

data class CoinCapAssetItem(
    val symbol: String? = null,
    val volumeUsd24Hr: Double? = null,
    val marketCapUsd: Double? = null,
    val priceUsd: Double? = null,
    val vwap24Hr: Double? = null,
    val changePercent24Hr: Double? = null,
    val name: String? = null,
    val rank: String? = null,
    val id: String? = null,
    val maxSupply: Double? = null,
    val supply: Double? = null
)
