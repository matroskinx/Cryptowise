package com.kvladislav.cryptowise.utils

import com.kvladislav.cryptowise.R
import java.net.UnknownHostException

fun handleException(e: Exception): Int {
    return when (e) {
        is UnknownHostException -> R.string.no_connection_error
        else -> R.string.unknown_error
    }
}
