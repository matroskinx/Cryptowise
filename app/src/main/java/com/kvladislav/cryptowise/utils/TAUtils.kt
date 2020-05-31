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

        // Calculates using every given candle
        fun stochasticOscillator(candles: List<CandleItem>, N: Int): List<Float> {
            val candleSublists: MutableList<List<CandleItem>> = mutableListOf()
            for (i in 0..candles.count() - N) {
                candleSublists.add(candles.subList(i, i + N))
            }
            val stochastics = mutableListOf<Float>()
            candleSublists.forEach {
                stochastics.add(findStochasticK(it))
            }
            return stochastics
        }

        private fun findStochasticK(candles: List<CandleItem>): Float {
            val C: Double = candles.last().close ?: 0.0
            val LN: Double = candles.minBy { it.close ?: 0.0 }?.close ?: 0.0
            val HN: Double = candles.maxBy { it.close ?: 0.0 }?.close ?: 0.0
            val K = (C - LN) / (HN - LN) * 100
            return K.toFloat()
        }
    }
}