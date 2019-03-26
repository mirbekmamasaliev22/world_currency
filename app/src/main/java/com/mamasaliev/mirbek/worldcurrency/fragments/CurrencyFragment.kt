package com.mamasaliev.mirbek.worldcurrency.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mamasaliev.mirbek.worldcurrency.R
import com.mamasaliev.mirbek.worldcurrency.adapters.CurrencyRecyclerAdapter
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import com.mamasaliev.mirbek.worldcurrency.helper.CurrencyAPI
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


class CurrencyFragment: Fragment(){
    private var banks: ArrayList<BankRate> = ArrayList<BankRate>()
    private var listener: OnCurrencyFragmentListener? = null

    private lateinit var bankInfoSubscription: Subscription
    private lateinit var rvList: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var rgOperation: RadioGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var currencies: List<String>
    private lateinit var tvBanksCount: TextView

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_currency, container, false)

        initViewElements(v)

        currencies = listOf(resources.getString(R.string.eur), resources.getString(R.string.usd), resources.getString(R.string.rub), resources.getString(R.string.kzt))

        // Spinner
        val spinnerAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, currencies)
        spinner.adapter = spinnerAdapter

        // Recycler view
        val rvAdapter       = CurrencyRecyclerAdapter(banks, context!!)
        val rvLayoutManager   = LinearLayoutManager(context!!, LinearLayout.VERTICAL, false)

        rvAdapter.onClickItemObservable(BankRate::class.java).subscribe{ bank -> listener?.onBankSelected("EUR", true, bank, banks)}
        rvLayoutManager.stackFromEnd = true

        rvList.layoutManager = rvLayoutManager
        rvList.adapter = rvAdapter

        // Listeners
        rgOperation.setOnCheckedChangeListener { _, _ -> onSortListByParams() }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onSortListByParams()
            }
        }

        // Getting data
        val helper = CurrencyAPI()
        helper.allBanks(
            object : Subscriber<BankRate> {
                override fun onComplete() {
                    progressBar.visibility = ProgressBar.INVISIBLE
                    Log.d("Flowable", "onComplete: ${rvAdapter.banks.size}")
                    tvBanksCount.text = "${rvAdapter.banks.size}"
                }

                override fun onSubscribe(s: Subscription) {
                    Log.d("Flowable", "onSubscribe")
                    progressBar.visibility = ProgressBar.VISIBLE
                    rvAdapter.banks.clear()
                    bankInfoSubscription = s
                    bankInfoSubscription.request(1)
                }

                override fun onNext(t: BankRate?) {
                    Log.d("Flowable", "get: ${t!!.bankInfo.id}")
                    rvAdapter.addItem(t)
                    tvBanksCount.text = "${rvAdapter.banks.size}"
                    bankInfoSubscription.request(1)
                }

                override fun onError(t: Throwable?) {
                    progressBar.visibility = ProgressBar.INVISIBLE
                    tvBanksCount.text = "${rvAdapter.banks.size}?"
                    Log.d("Flowable", "onError = ${t!!.message}")
                }

            })
        return v
    }


    private fun initViewElements(v: View) {
        rvList = v.findViewById(R.id.banksList)
        spinner = v.findViewById(R.id.spinnerCurrency)
        rgOperation = v.findViewById(R.id.rgOperation)
        progressBar = v.findViewById(R.id.progressBar)
        tvBanksCount = v.findViewById(R.id.tvBanksCount)
    }

    @SuppressLint("CheckResult")
    private fun onSortListByParams() {
        val isBuy = rgOperation.checkedRadioButtonId == R.id.rbBuy
        val currency = currencyName(currencies[spinner.selectedItemPosition])
        val sortedBanks = sort(isBuy, currency)
        if (sortedBanks.size != 0 ) {
            val newAdapter = CurrencyRecyclerAdapter(sortedBanks, context!!)
            newAdapter.onClickItemObservable(BankRate::class.java)
                .subscribe{bank -> listener?.onBankSelected(currency, isBuy, bank, banks)}
            rvList.adapter = newAdapter
            rvList.scrollToPosition(0)
        }
    }

    private fun currencyName(resource: String): String = when(resource) {
        resources.getString(R.string.usd) -> "USD"
        resources.getString(R.string.eur) -> "EUR"
        resources.getString(R.string.rub) -> "RUB"
        else -> "KZT"
    }

    private fun sort(isBuy: Boolean, currencyName: String): ArrayList<BankRate> {

        banks.sortWith(Comparator { b1, b2 ->
            val currency1 = b1.getCurrency(currencyName)
            val currency2 = b2.getCurrency(currencyName)

            if (currency1 == null && currency2 == null) {
                return@Comparator 0
            }

            if (currency1 == null){
                return@Comparator 1
            }

            if (currency2 == null){
                return@Comparator -1
            }

            if (isBuy) {
                val f = currency1.sell
                val s = currency2.sell

                Log.d("comparing", "$currencyName buy $f, $s: ${f.compareTo(s)}")

                return@Comparator f.compareTo(s)
            } else {
                val f = currency1.buy
                val s = currency2.buy

                Log.d("comparing", "$currencyName sell $f, $s: ${f.compareTo(s)}")
                return@Comparator f.compareTo(s) * -1
            }
        })

        return banks
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCurrencyFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }
    
    interface OnCurrencyFragmentListener{
        fun onBankSelected(currencyName: String, isBuy: Boolean, bank: BankRate, banks: ArrayList<BankRate>)
    }

    override fun onDetach() {
        bankInfoSubscription.cancel()
        super.onDetach()
    }

    override fun onDestroyView() {
        bankInfoSubscription.cancel()
        super.onDestroyView()
    }
}
