package com.kvladislav.cryptowise.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    val activityActionBehavior = SingleLiveEvent<VmAction>()

    protected fun withActivity(block: (AppCompatActivity) -> Unit) {
        VmActionCompat(block).invokeAction()
    }

    private fun VmAction.invokeAction() {
        val isUiThread = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            android.os.Looper.getMainLooper().isCurrentThread
        } else {
            Thread.currentThread() === android.os.Looper.getMainLooper().thread
        }

        if (isUiThread) {
            activityActionBehavior.value = this
        } else {
            activityActionBehavior.postValue(this)
        }
    }
}