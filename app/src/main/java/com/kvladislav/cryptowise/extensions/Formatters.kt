package com.kvladislav.cryptowise.extensions

fun Double.formatWithPercent(digits: Int): String {
    return if (this > 0) {
        "+%.${digits}f%%".format(this)
    } else {
        "%.${digits}f%%".format(this)
    }
}

fun Double.formatDigits(digits: Int): String {
    return "%.${digits}f".format(this)
}

