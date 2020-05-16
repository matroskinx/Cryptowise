package com.kvladislav.cryptowise.models.portfolio

data class DisplayPortfolioItem(val portfolioItem: PortfolioItem, val itemPrice: Double) {
    val holdValue: Double
        get() = itemPrice * portfolioItem.assetAmount
}