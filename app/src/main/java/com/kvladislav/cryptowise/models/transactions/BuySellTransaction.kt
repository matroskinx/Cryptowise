package com.kvladislav.cryptowise.models.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kvladislav.cryptowise.database.TransactionTypeConverter
import com.kvladislav.cryptowise.enums.TransactionType


@Entity(tableName = "transaction_table")
@TypeConverters(TransactionTypeConverter::class)
data class BuySellTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: TransactionType,
    val timestamp: Long,
    val usdPerCoin: Double,
    val coinQuantity: Double,
    val fee: Double,
    val cmcId: Int,
    val cmcSymbol: String,
    val userNote: String
)