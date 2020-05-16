package com.kvladislav.cryptowise.enums

enum class TAType(val friendlyName: String, val position: Int) {
    SIMPLE_MOVING_AVERAGE("Simple moving average", 0),
    EXP_MOVING_AVERAGE("Exponential moving average", 1),
    OSCILLATOR("Oscillator", 2)
}