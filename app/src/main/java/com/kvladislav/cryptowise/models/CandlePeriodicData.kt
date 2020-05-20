package com.kvladislav.cryptowise.models

import com.kvladislav.cryptowise.enums.TimeInterval
import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem

data class CandlePeriodicData(
    val cmcId: Int,
    val dataH1: List<CandleItem>,
    val dataH8: List<CandleItem>,
    val dataD1: List<CandleItem>
) {
    fun getCandlesFor(timeInterval: TimeInterval): List<CandleItem> {
        return when (timeInterval) {
            TimeInterval.DAY -> dataH1
            TimeInterval.WEEK -> dataH8
            TimeInterval.MONTH,
            TimeInterval.MONTH_3,
            TimeInterval.MONTH_6,
            TimeInterval.YEAR -> dataD1
        }
    }
}