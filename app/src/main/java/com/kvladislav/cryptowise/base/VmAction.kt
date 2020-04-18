package com.kvladislav.cryptowise.base

import androidx.appcompat.app.AppCompatActivity

open class VmAction(var singleAction: (BaseActivity) -> Unit) {

    open fun invoke(activity: AppCompatActivity?) {
        activity ?: return

        (activity as BaseActivity).let {
            singleAction(it)
        }
        singleAction = {}
    }
}

class VmActionCompat(
    var action: (AppCompatActivity) -> Unit
) : VmAction({}) {

    override fun invoke(activity: AppCompatActivity?) {
        activity ?: return
        action(activity)
        action = {}
    }
}