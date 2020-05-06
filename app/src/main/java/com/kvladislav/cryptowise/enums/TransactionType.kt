package com.kvladislav.cryptowise.enums

import android.content.Context
import com.kvladislav.cryptowise.R

enum class TransactionType(val code: Int) {
    BUY(0), SELL(1), TRANSFER(2);

    fun getFriendlyName(context: Context): String {
        return when (this) {
            BUY -> context.getString(R.string.buy)
            SELL -> context.getString(R.string.sell)
            TRANSFER -> context.getString(R.string.transfer)
        }
    }

}
