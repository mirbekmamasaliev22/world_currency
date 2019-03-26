package com.mamasaliev.mirbek.worldcurrency.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mamasaliev.mirbek.worldcurrency.R
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import java.text.NumberFormat
import java.util.*


class ConverterFragment : DialogFragment() {
    private val ARG_CURRENCY_NAME = "currency_name"
    private val ARG_IS_BUY = "isBuy"
    private val ARG_BANKS = "banks"
    private val ARG_SELECTED_BANK_INDEX = "selected_bank_index"

    private lateinit var banks: ArrayList<BankRate>
    private var selectedBankIndex: Int = 0
    private var currencyName: String = ""
    private var isBuy: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencyName = it.getString(ARG_CURRENCY_NAME)!!
            isBuy = it.getBoolean(ARG_IS_BUY)
            banks = it.getParcelableArrayList<BankRate>(ARG_BANKS)!!
            selectedBankIndex = it.getInt(ARG_SELECTED_BANK_INDEX)
            Log.d("DATA", "${currencyName} ${isBuy}")
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_converter, container, false)

        val btnClose = v.findViewById<Button>(R.id.btnClose)
        val spinnerBanks:Spinner = v.findViewById(R.id.spinnerBanks)
        val spinnerCurrencyFrom = v.findViewById<Spinner>(R.id.spFrom)
        val spinnerCurrencyTo = v.findViewById<Spinner>(R.id.spTo)
        val etMoney = v.findViewById<EditText>(R.id.etMoney)
        val btnChange = v.findViewById<ImageButton>(R.id.btnChange)
        val tvResult = v.findViewById<TextView>(R.id.tvResult)
        val tvFromCurrency = v.findViewById<TextView>(R.id.tvFromCurrency)

        btnClose.clicks().subscribe{dismiss()}

        // Spinner data
        val currencyOthers = listOf(resources.getString(R.string.eur), resources.getString(R.string.usd), resources.getString(R.string.rub), resources.getString(R.string.kzt))
        val currencySom = listOf(resources.getString(R.string.som))
        val banksList = arrayListOf<String>()
        for (bank in banks) banksList.add(bank.bankInfo.name)

        // Adapters
        val adapterBanks = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, banksList)
        val adapterCurrencyOthers = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, currencyOthers)
        val adapterCurrencySom = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, currencySom)


        // Spinner Banks
        spinnerBanks.adapter = adapterBanks
        spinnerBanks.setSelection(selectedBankIndex)

        // Spinner From and To
        val indexSelectedCurrency = when (currencyName.toLowerCase()) {"eur" -> 0; "usd" -> 1; "rub" -> 2 else -> 3}
        if (isBuy) {
            spinnerCurrencyFrom.adapter = adapterCurrencySom
            spinnerCurrencyTo.adapter = adapterCurrencyOthers
            spinnerCurrencyTo.setSelection(indexSelectedCurrency)
        } else {
            spinnerCurrencyFrom.adapter = adapterCurrencyOthers
            spinnerCurrencyFrom.setSelection(indexSelectedCurrency)
            spinnerCurrencyTo.adapter = adapterCurrencySom
        }

        // Listeners change button
        btnChange.clicks().subscribe{
            val adapterOthers = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, currencyOthers)
            val adapterSom = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, currencySom)

            if (spinnerCurrencyFrom.adapter.count > 1){
                spinnerCurrencyTo.adapter = adapterOthers
                spinnerCurrencyTo.setSelection(spinnerCurrencyFrom.selectedItemPosition)

                spinnerCurrencyFrom.adapter = adapterSom
            } else {
                spinnerCurrencyFrom.adapter = adapterOthers
                spinnerCurrencyFrom.setSelection(spinnerCurrencyTo.selectedItemPosition)

                spinnerCurrencyTo.adapter = adapterSom
            }

        }

        // Listeners spinner From
        RxAdapterView.itemSelections(spinnerCurrencyFrom)
            .subscribe({
                etMoney.text = Editable.Factory.getInstance().newEditable("")
                tvResult.text = ""
                tvFromCurrency.text = spinnerCurrencyFrom.selectedItem.toString()
            },{e -> Log.d("error", e.message)})

        // Listeners spinner banks
        RxAdapterView.itemSelections(spinnerBanks)
            .subscribe({
                selectedBankIndex = spinnerBanks.selectedItemPosition
                etMoney.text = Editable.Factory.getInstance().newEditable("")
                tvResult.text = ""
                tvFromCurrency.text = spinnerCurrencyFrom.selectedItem.toString()
            },{e -> Log.d("error", e.message)})

        // Listeners edit text money
        RxTextView.textChanges(etMoney).subscribe({
            try {
                Log.d("text_change", "Changing")
                val value = etMoney.text.toString().replace(",", ".").toDouble()
                val currencyFrom = spinnerCurrencyFrom.selectedItem
                val currencyTo = spinnerCurrencyTo.selectedItem
                val res: Double

                if (currencyFrom == resources.getString(R.string.som)) {
                    val currencyCoefficient = when (currencyTo) {
                        resources.getString(R.string.usd) -> banks[selectedBankIndex].getCurrency("usd")?.sell!!
                        resources.getString(R.string.eur) -> banks[selectedBankIndex].getCurrency("eur")?.sell!!
                        resources.getString(R.string.rub) -> banks[selectedBankIndex].getCurrency("rub")?.sell!!
                        else -> banks[selectedBankIndex].getCurrency("kzt")?.sell!!
                    }
                    res = value / currencyCoefficient
                } else {
                    val currencyCoefficient = when (currencyFrom) {
                        resources.getString(R.string.usd) -> banks[selectedBankIndex].getCurrency("usd")?.buy!!
                        resources.getString(R.string.eur) -> banks[selectedBankIndex].getCurrency("eur")?.buy!!
                        resources.getString(R.string.rub) -> banks[selectedBankIndex].getCurrency("rub")?.buy!!
                        else -> banks[selectedBankIndex].getCurrency("kzt")?.buy!!
                    }
                    res = value * currencyCoefficient
                }

                val format = NumberFormat.getCurrencyInstance(Locale.US)
                tvResult.text = if (etMoney.text.toString() == "") "" else String.format(
                    "%s %s",
                    format.format(res).replace("$", ""),
                    currencyTo
                )
            }catch (e: NumberFormatException){
                Log.d("error", e.message)
            }
        },{ e -> Log.d("error", "Calculating currecy: ${e.message}")})

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(currencyName:String, isBuy: Boolean, banks: ArrayList<BankRate>, index: Int) =
            ConverterFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_BANKS, banks)
                    putInt(ARG_SELECTED_BANK_INDEX, index)
                    putString(ARG_CURRENCY_NAME, currencyName)
                    putBoolean(ARG_IS_BUY, isBuy)
                }
            }
    }
}
