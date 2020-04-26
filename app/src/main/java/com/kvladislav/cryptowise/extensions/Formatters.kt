package com.kvladislav.cryptowise.extensions

fun Double.format(digits: Int) = "%.${digits}f".format(this)