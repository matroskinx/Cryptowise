package com.kvladislav.cryptowise.models.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transaction_table")
data class BuySellTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timestamp: Long,
    val usdPerCoin: Double,
    val coinQuantity: Double,
    val fee: Double,
    val cmcId: Int,
    val cmcSymbol: String,
    val userNote: String
)