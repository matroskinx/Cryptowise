package com.kvladislav.cryptowise.utils

import com.kvladislav.cryptowise.models.coin_cap.candles.CandleItem

class TAUtils {
    companion object {
        fun candlesAverage(candles: List<CandleItem>): Float {
            var sum = 0f
            candles.forEach {
                sum += it.close?.toFloat() ?: 0f
            }
            return sum / candles.count()
        }

        fun simpleMovingAverage(candles: List<CandleItem>, period: Int): List<Float> {
            val candlesPeriodic: MutableList<List<CandleItem>> = mutableListOf()

            for (x in 0..candles.count() - period) {
                candlesPeriodic.add(candles.subList(x, x + period))
            }

            val movingAverages = mutableListOf<Float>()

            candlesPeriodic.forEach {
                movingAverages.add(candlesAverage(it))
            }
            return movingAverages
        }
    }
}