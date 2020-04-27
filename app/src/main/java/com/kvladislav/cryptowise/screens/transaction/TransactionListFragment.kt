package com.kvladislav.cryptowise.screens.transaction

import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.extensions.transaction
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber

class TransactionListFragment : BaseFragment(R.layout.fragment_transaction_list) {
    private lateinit var adapter: ListDelegationAdapter<List<BuySellTransaction>>

    override fun viewModel(): TransactionListViewModel = getSharedViewModel()

    override fun setupView() {
        setupAdapter()
    }

    override fun setupListeners() {
        add_button.setOnClickListener {
            parentFragmentManager.transaction {
                addToBackStack(AddFragment::class.java.canonicalName)
                replace(R.id.fragment_container, AddFragment())
            }
        }
    }

    override fun setupObservers() {
        viewModel().allTransactions.observe(viewLifecycleOwner) {
            Timber.d("Triggered observer: $it")
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupAdapter() {
        val transactionAdapter =
            adapterDelegateLayoutContainer<BuySellTransaction, BuySellTransaction>(R.layout.transaction_rv_item) {
                bind {
                    // TODO() binding
                }
            }

        adapter = ListDelegationAdapter<List<BuySellTransaction>>(transactionAdapter)
        transaction_rv.layoutManager = LinearLayoutManager(context)
        transaction_rv.adapter = adapter
    }
}