package com.kvladislav.cryptowise.screens.authorization.registration

import androidx.core.view.isVisible
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import kotlinx.android.synthetic.main.fragment_registration.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RegistrationFragment : BaseFragment(R.layout.fragment_registration) {
    override fun viewModel(): RegistrationViewModel = getViewModel()

    override fun setupListeners() {
        register_button.setOnClickListener {
            val email = email_et.text.toString()
            val password = password_et.text.toString()
            viewModel().onRegisterTap(email, password)
        }
    }

    override fun setupObservers() {
        viewModel().isLoading.observe(viewLifecycleOwner) { isLoading ->
            input_ll.isVisible = !isLoading
            register_button.isVisible = !isLoading
            progress_layout.isVisible = isLoading
        }
    }
}