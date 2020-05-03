package com.kvladislav.cryptowise

class DataStorage {
    val trustworthyCoinIds = hashSetOf<String>()

    fun setTrustworthyCoins(coins: HashSet<String>) {
        trustworthyCoinIds.clear()
        trustworthyCoinIds.addAll(coins)
    }

    companion object {
        val TRUSTWORTHY_PROVIDERS =
            setOf("bigone", "poloniex", "binance", "kucoin", "coinex", "exmo", "huobi")
    }

}