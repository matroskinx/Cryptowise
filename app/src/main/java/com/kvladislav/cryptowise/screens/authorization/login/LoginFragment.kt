package com.kvladislav.cryptowise.screens.authorization.login

import androidx.core.view.isVisible
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseActivity
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    override fun viewModel(): LoginViewModel = getViewModel()

    override fun setupListeners() {
        login_button.setOnClickListener {
            var hasError = false
            val email = email_et.text.toString()
            if (email.isEmpty()) {
                email_et.error = "Incorrect email"
                hasError = true
            }
            val password = password_et.text.toString()
            if (password.isEmpty()) {
                password_et.error = "Incorrect password"
                hasError = true
            }
            if (!hasError) {
                (activity as? BaseActivity)?.hideSoftKeyboard()
                viewModel().onLoginTap(email, password)
            }
        }

        register_invite_button.setOnClickListener {
            viewModel().onRegisterInvitationTap()
        }
    }

    override fun setupObservers() {
        viewModel().isLoading.observe(viewLifecycleOwner) { isLoading ->
            input_ll.isVisible = !isLoading
            register_invite_button.isVisible = !isLoading
            login_button.isVisible = !isLoading
            progress_layout.isVisible = isLoading
        }
    }
}