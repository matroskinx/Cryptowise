package com.kvladislav.cryptowise

import android.os.Bundle
import com.kvladislav.cryptowise.base.BaseActivity
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.splash.SplashFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        supportFragmentManager.transaction {
            this.replace(
                fragment_container.id,
                SplashFragment()
            )
        }
    }
}
