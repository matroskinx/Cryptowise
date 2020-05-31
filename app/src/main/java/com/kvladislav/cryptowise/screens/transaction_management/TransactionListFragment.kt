package com.kvladislav.cryptowise.screens.transaction_management

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.kvladislav.cryptowise.screens.AppViewModel
import com.kvladislav.cryptowise.utils.FormatterUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.transaction_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransactionListFragment : BaseFragment(R.layout.fragment_transaction_list) {
    private lateinit var adapter: ListDelegationAdapter<List<BuySellTransaction>>

    private lateinit var appViewModel: AppViewModel;

    override fun viewModel(): TransactionListViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = getSharedViewModel()
    }

    override fun setupView() {
        setupAdapter()
    }

    override fun setupObservers() {
        viewModel().allTransactions.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
            setupEmptyTransactionsView(it.isEmpty())
        }
    }

    private fun setupEmptyTransactionsView(isVisible: Boolean) {
        transactions_empty_layout.isVisible = isVisible
    }

    private fun setupAdapter() {
        val transactionAdapter =
            adapterDelegateLayoutContainer<BuySellTransaction, BuySellTransaction>(R.layout.transaction_rv_item) {
                bind {
                    val item = this.item
                    operation_tv.text = item.type.getFriendlyName(context)
                    quantity_tv.text = "${item.coinQuantity} ${item.cmcSymbol}"
                    date_tv.text = FormatterUtils.formatDate(item.timestamp)
                    val operation = when (item.type) {
                        TransactionType.BUY -> "Paid"
                        TransactionType.SELL -> "Received"
                        TransactionType.TRANSFER -> "Transferred"
                    }
                    val operationText = "$operation ${item.usdPerCoin * item.coinQuantity}$"
                    usd_tv.text = operationText
                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${item.cmcId}.png")
                        .into(operation_iv)

                    clear_button.setOnClickListener {
                        viewModel().onItemClicked(item)
                    }
                }
            }

        adapter = ListDelegationAdapter(transactionAdapter)
        transaction_rv.layoutManager = LinearLayoutManager(context)
        transaction_rv.adapter = adapter
    }

}