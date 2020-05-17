package com.kvladislav.cryptowise.enums

enum class TimeInterval {
    DAY, WEEK, MONTH, MONTH_3, MONTH_6, YEAR;

    companion object {
        fun getCandleCount(interval: TimeInterval): Int {
            return when (interval) {
                DAY -> 24
                WEEK -> 21
                MONTH -> 30
                MONTH_3 -> 90
                MONTH_6 -> 180
                YEAR -> 365
            }
        }
    }
}