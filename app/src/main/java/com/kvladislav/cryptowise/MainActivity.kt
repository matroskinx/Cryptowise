package com.kvladislav.cryptowise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.overview.OverviewFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        supportFragmentManager.transaction {
            this.replace(
                fragment_container.id,
                OverviewFragment()
            )
        }
    }
}
