package com.kvladislav.cryptowise.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    abstract fun viewModel(): BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeVmActions(viewModel())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        setupObservers()
    }

    open fun setupListeners() {}

    open fun setupObservers() {}

    open fun setupView() {}

    private fun observeVmActions(vm: BaseViewModel) {
        vm.activityActionBehavior.observe(this@BaseFragment, Observer {
            it?.invoke(activity as? AppCompatActivity ?: return@Observer)
        })
    }
}