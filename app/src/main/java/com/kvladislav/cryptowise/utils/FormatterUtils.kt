package com.kvladislav.cryptowise.utils

import java.text.SimpleDateFormat
import java.util.*


class FormatterUtils {
    companion object {
        private val suffixes: NavigableMap<Long, String> = TreeMap()
        private val c = charArrayOf('K', 'M', 'B', 'T')

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

        fun formatDate(timestamp: Long): String {
            val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return fmt.format(timestamp)
        }


        /**
         * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
         * @param n the number to format
         * @param iteration in fact this is the class from the array c
         * @return a String representing the number n formatted in a cool looking way.
         */
        fun coolFormat(n: Double, iteration: Int): String? {
            val d = n.toLong() / 100 / 10.0
            val isRound =
                d * 10 % 10 == 0.0 //true if the decimal part is equal to 0 (then it's trimmed anyway)
            return if (d < 1000) //this determines the class, i.e. 'k', 'm' etc
                (if (d > 99.9 || isRound || !isRound && d > 9.99) //this decides whether to trim the decimals
                    d.toInt() * 10 / 10 else d.toString() + "" // (int) d * 10 / 10 drops the decimal
                        ).toString() + "" + c[iteration] else coolFormat(d, iteration + 1)
        }

    }
}