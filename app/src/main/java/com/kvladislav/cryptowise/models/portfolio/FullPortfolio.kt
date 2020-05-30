package com.kvladislav.cryptowise.models.portfolio

data class FullPortfolio(val value: Double, val assets: List<DisplayPortfolioItem>) {
    val isEmpty: Boolean
        get() = assets.isEmpty()
}