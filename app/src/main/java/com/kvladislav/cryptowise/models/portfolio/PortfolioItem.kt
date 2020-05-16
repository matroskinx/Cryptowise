package com.kvladislav.cryptowise.models.portfolio

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio_records")
data class PortfolioItem(
    @PrimaryKey
    val coinCapId: String,
    val iconId: Int,
    val symbol: String,
    var assetAmount: Double
)