package com.kvladislav.cryptowise.screens.authorization.login

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.authorization.registration.RegistrationFragment
import com.kvladislav.cryptowise.screens.overview.OverviewFragment
import timber.log.Timber

class LoginViewModel(private val context: Context) : BaseViewModel() {
    val isLoading = MutableLiveData<Boolean>(false)

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onRegisterInvitationTap() {
        withActivity {
            it.supportFragmentManager.transaction {
                this.addToBackStack(RegistrationFragment::class.java.canonicalName)
                this.replace(R.id.fragment_container, RegistrationFragment())
            }
        }
    }

    fun onLoginTap(email: String, password: String) {
        isLoading.postValue(true)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isLoading.postValue(false)
                openOverViewScreen()
            } else {
                Timber.e(task.exception)
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
                isLoading.postValue(false)
            }
        }
    }

    private fun openOverViewScreen() {
        withActivity {
            it.supportFragmentManager.run {
                this.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                this.transaction {
                    this.addToBackStack(OverviewFragment::class.java.canonicalName)
                    this.replace(R.id.fragment_container, OverviewFragment())
                }
            }
        }
    }
}