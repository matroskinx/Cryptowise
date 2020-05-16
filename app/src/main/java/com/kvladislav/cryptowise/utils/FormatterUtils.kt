package com.kvladislav.cryptowise.utils

import java.util.*


class FormatterUtils {
    companion object {
        private val suffixes: NavigableMap<Long, String> = TreeMap()

        init {
            suffixes[1_000L] = "k"
            suffixes[1_000_000L] = "M"
            suffixes[1_000_000_000L] = "G"
            suffixes[1_000_000_000_000L] = "T"
            suffixes[1_000_000_000_000_000L] = "P"
            suffixes[1_000_000_000_000_000_000L] = "E"
        }

        fun format(value: Long): String {
            if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1)
            if (value < 0) return "-" + format(-value)
            if (value < 1000) return value.toString() //deal with easy case
            val e = suffixes.floorEntry(value)!!
            val divideBy = e.key
            val suffix = e.value
            val truncated =
                value / (divideBy / 10) //the number part of the output times 10
            val hasDecimal =
                truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
            return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
        }

    }
}