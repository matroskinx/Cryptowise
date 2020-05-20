package com.kvladislav.cryptowise.models

import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem

data class CandlePeriodicData(
    val cmcId: Int,
    val dataH1: List<CandleItem>,
    val dataH8: List<CandleItem>,
    val dataD1: List<CandleItem>
)