package com.kvladislav.cryptowise.screens.transaction

import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.extensions.observe
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.transaction_rv_item.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TransactionListFragment : BaseFragment(R.layout.fragment_transaction_list) {
    private lateinit var adapter: ListDelegationAdapter<List<BuySellTransaction>>

    override fun viewModel(): TransactionListViewModel = getSharedViewModel()

    override fun setupView() {
        setupAdapter()
    }

    override fun setupObservers() {
        viewModel().allTransactions.observe(viewLifecycleOwner) {
            Timber.d("Triggered observer: $it")
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun formatDate(timestamp: Long): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return fmt.format(timestamp)
    }

    private fun setupAdapter() {
        val transactionAdapter =
            adapterDelegateLayoutContainer<BuySellTransaction, BuySellTransaction>(R.layout.transaction_rv_item) {
                bind {
                    val item = this.item
                    operation_tv.text = item.type.getFriendlyName(context)
                    quantity_tv.text = "${item.coinQuantity} ${item.cmcSymbol}"
                    date_tv.text = formatDate(item.timestamp)

                    val operation = when (item.type) {
                        TransactionType.BUY -> "Paid"
                        TransactionType.SELL -> "Received"
                        TransactionType.TRANSFER -> "Transferred"
                    }

                    usd_tv.text = "$operation ${item.usdPerCoin}$"

                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${item.cmcId}.png")
                        .into(operation_iv)
                }
            }

        adapter = ListDelegationAdapter<List<BuySellTransaction>>(transactionAdapter)
        transaction_rv.layoutManager = LinearLayoutManager(context)
        transaction_rv.adapter = adapter
    }
}