package com.kvladislav.cryptowise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.kvladislav.cryptowise.models.cmc_map.DataItem
import com.kvladislav.cryptowise.repositories.OverviewRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mock_item.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val overviewRepository = OverviewRepository()

    lateinit var adapter: ListDelegationAdapter<List<DataItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupAdapter()
        GlobalScope.launch {
            try {
                val response = overviewRepository.getIDMap()
                Log.d("TTT", "response: $response)")

                response.data?.let {

                    Log.d("TTT", "setting")

                    adapter.items = it
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.d("Caught exception", "$e")
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setupAdapter()
    }

    private fun setupAdapter() {
        val currencyAdapter =
            adapterDelegateLayoutContainer<DataItem, DataItem>(R.layout.mock_item) {
                bind {
                    name.text = this.item.name
                    Picasso.get()
                        .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${this.item.id}.png")
                        .into(logo)
                }
            }

        adapter = ListDelegationAdapter<List<DataItem>>(currencyAdapter)
        cur_rv.layoutManager = LinearLayoutManager(this)
        cur_rv.adapter = adapter
    }
}
