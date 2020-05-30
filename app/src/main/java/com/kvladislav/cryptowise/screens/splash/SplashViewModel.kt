package com.kvladislav.cryptowise.screens.splash

import android.os.Handler
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.authorization.login.LoginFragment
import com.kvladislav.cryptowise.screens.overview.OverviewFragment
import timber.log.Timber

class SplashViewModel : BaseViewModel() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        withActivity {
            it.supportActionBar?.hide()
        }
    }

    fun handleLaunch() {
        Handler().postDelayed({
            if (firebaseAuth.currentUser != null) {
                Timber.d("Authorized")
                withActivity {
                    it.supportFragmentManager.run {
                        this.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.transaction {
                            this.replace(
                                R.id.fragment_container,
                                OverviewFragment()
                            )
                        }
                    }
                }
            } else {
                Timber.d("Not authorized")
                withActivity {
                    it.supportFragmentManager.run {
                        this.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        this.transaction { this.replace(R.id.fragment_container, LoginFragment()) }
                    }
                }
            }
        }, 500)
    }
}