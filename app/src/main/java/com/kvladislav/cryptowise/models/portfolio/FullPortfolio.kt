package com.kvladislav.cryptowise.models.portfolio

data class FullPortfolio(val value: Double, val assets: List<DisplayPortfolioItem>) {
    fun calculatePositiveSum(): Double {
        var sum = 0.0;
        assets.forEach { asset ->
            if (asset.holdValue > 0)
                sum += asset.holdValue
        }
        return sum
    }

    val isEmpty: Boolean
        get() = assets.isEmpty()
}