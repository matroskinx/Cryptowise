package com.kvladislav.cryptowise.database

import androidx.room.TypeConverter
import com.kvladislav.cryptowise.enums.TransactionType
import java.lang.IllegalArgumentException

class TransactionTypeConverter {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): Int = value.code

    @TypeConverter
    fun toTransactionType(value: Int) : TransactionType = when (value) {
        TransactionType.BUY.code -> TransactionType.BUY
        TransactionType.SELL.code -> TransactionType.SELL
        TransactionType.TRANSFER.code -> TransactionType.TRANSFER
        else -> throw IllegalArgumentException("Unable to parse value $value")
    }
}