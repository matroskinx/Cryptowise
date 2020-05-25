package com.kvladislav.cryptowise.models

data class BuySellForm(
    val price: Double,
    val quantity: Double,
    val fee: Double,
    val timestamp: Long
)