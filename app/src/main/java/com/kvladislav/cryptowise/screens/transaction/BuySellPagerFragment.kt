package com.kvladislav.cryptowise.screens.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseFragment
import com.kvladislav.cryptowise.enums.TransactionType
import com.kvladislav.cryptowise.models.CMCDataMinified
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_buy_sell_pager.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class BuySellPagerFragment : BaseFragment(R.layout.fragment_buy_sell_pager) {
    override fun viewModel(): BuySellPagerViewModel =
        getViewModel { parametersOf(parseArguments(arguments)) }

    private val calendar = Calendar.getInstance()

    private fun parseArguments(arguments: Bundle?): CMCDataMinified {
        return arguments?.run {
            CMCDataMinified(
                getInt(AddFragment.CMC_ID_EXTRA),
                getString(AddFragment.CMC_SYMBOL_EXTRA, ""),
                getString(AddFragment.COINCAP_ID_EXTRA, "")
            )
        } ?: throw IllegalArgumentException("Bundle was null during initialization")
    }

    override fun setupView() {
        when (viewModel().currentType) {
            TransactionType.BUY -> setupBuyView()
            TransactionType.SELL -> setupSellView()
            else -> throw IllegalStateException("Unable to launch buy sell fragment for ${viewModel().currentType}")
        }
        setupBasicView()
        setupDropdown()
    }

    private fun setupBasicView() {
        val basicData = parseArguments(arguments)
        Picasso.get()
            .load("https://s2.coinmarketcap.com/static/img/coins/128x128/${basicData.cmcId}.png")
            .into(crypto_iv)
        crypto_tv.text = basicData.symbol
    }

    private fun setupDropdown() {
        val operations = arrayOf("Buy", "Sell")
        context?.let {
            val adapter =
                ArrayAdapter(it, R.layout.dropdown_item, operations)
            filled_exposed_dropdown.setAdapter(adapter)
        }

        filled_exposed_dropdown.setText(operations[0], false)

        filled_exposed_dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> viewModel().onOperationSet(TransactionType.BUY)
                1 -> viewModel().onOperationSet(TransactionType.SELL)
            }
        }

    }

    private fun setupDateField(date: Long) {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = fmt.format(date)
        date_et.setText(formattedDate)
    }

    override fun setupListeners() {
        date_et.setOnClickListener {
            Timber.d("CLICK")
            val dateListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    Timber.d("Picked date: $year $month $dayOfMonth")
                    calendar.set(year, month, dayOfMonth)
                    setupDateField(calendar.timeInMillis)
                }

            context?.let {
                val dateDialog = DatePickerDialog(
                    it,
                    dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                dateDialog.datePicker.maxDate = System.currentTimeMillis()
                dateDialog.show()
            }

        }

        action_button.setOnClickListener {
            createModel()?.let {
                viewModel().onActionTap(it)
            } ?: Toast.makeText(context, "Check fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun createModel(): BuySellForm? {
        val price = if (price_et.text.isNullOrEmpty()) null else price_et.text.toString().toDouble()
        val quantity =
            if (quantity_et.text.isNullOrEmpty()) null else quantity_et.text.toString().toDouble()
        val fee = if (fee_et.text.isNullOrEmpty()) null else fee_et.text.toString().toDouble()


        return if (price != null && quantity != null && fee != null)
            BuySellForm(price, quantity, fee, calendar.timeInMillis)
        else null
    }

    private fun setupBuyView() {

    }

    private fun setupSellView() {

    }
}

data class BuySellForm(
    val price: Double,
    val quantity: Double,
    val fee: Double,
    val timestamp: Long
)

