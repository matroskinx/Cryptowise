package com.kvladislav.cryptowise.screens.authorization.registration

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.screens.overview.OverviewFragment
import timber.log.Timber

class RegistrationViewModel(private val context: Context) : BaseViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val isLoading = MutableLiveData<Boolean>(false)

    fun onRegisterTap(email: String, password: String) {
        isLoading.postValue(true)
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("Registered successfully")
                Timber.d("Current user: ${firebaseAuth.currentUser?.displayName}")
                isLoading.postValue(false)
                openOverViewScreen()
            } else {
                isLoading.postValue(false)
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
                Timber.e(task.exception)
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