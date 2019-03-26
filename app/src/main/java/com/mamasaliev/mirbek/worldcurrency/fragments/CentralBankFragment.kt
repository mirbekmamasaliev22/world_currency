package com.mamasaliev.mirbek.worldcurrency.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.mamasaliev.mirbek.worldcurrency.R
import com.mamasaliev.mirbek.worldcurrency.adapters.NBKRCurrencyAdapter
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import com.mamasaliev.mirbek.worldcurrency.entity.NBKRRate
import com.mamasaliev.mirbek.worldcurrency.entity.SimpleCurrency
import com.mamasaliev.mirbek.worldcurrency.interfaces.NBKRApi
import com.mamasaliev.mirbek.worldcurrency.settings.AppCurrency
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeoutException

class CentralBankFragment : Fragment() {

    private lateinit var nbkrDisposable: Disposable

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_central_bank, container, false)

        val rvList = v.findViewById<RecyclerView>(R.id.rvList)
        val progressBar = v.findViewById<ProgressBar>(R.id.nbkrProgressBar)

        val api = NBKRApi.create()

        val observable: Observable<ArrayList<SimpleCurrency>>?

        if (AppCurrency.isInternetConnected()) {

            observable = api.getDaily()
            .withLatestFrom(api.getWeekly(),
                BiFunction<NBKRRate, NBKRRate, ArrayList<SimpleCurrency>> { t1, t2 ->
                    val simpleCurrencies = ArrayList<SimpleCurrency>()

                    for (item in t1.currencies) {
                        simpleCurrencies.add(SimpleCurrency(item.code, this.currencyNames(item.code), item.value, item.nominal))
                    }
                    for (item in t2.currencies) {
                        simpleCurrencies.add(SimpleCurrency(item.code, this.currencyNames(item.code), item.value, item.nominal))
                    }
                    AppCurrency.appDatabase.appDao().insertCentralBankCurrencies(simpleCurrencies)
                simpleCurrencies
            })
        } else {
            observable = Observable.create { subscriber ->
                val simpleCurrencies = ArrayList<SimpleCurrency>()
                val arr = AppCurrency.appDatabase.appDao().getCentralBankCurrencyRate()
                for (a in arr) simpleCurrencies.add(a)
                subscriber.onNext(simpleCurrencies)
                subscriber.onComplete()
            }
        }
        observable!!
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBar.visibility = ProgressBar.VISIBLE }
            .subscribe({currencies ->
                progressBar.visibility = ProgressBar.INVISIBLE

                val adapter = NBKRCurrencyAdapter(currencies, context!!)
                val layoutManager = LinearLayoutManager(context!!, LinearLayout.VERTICAL, false)

                rvList.adapter = adapter
                rvList.layoutManager = layoutManager
            },{err ->
                Log.d("xmlError", err.message)
                progressBar.visibility = ProgressBar.INVISIBLE
            },{

            }, {t: Disposable? ->
                if (t != null)
                    nbkrDisposable = t
            })
        return v
    }

    private fun currencyNames(code: String) = when(code){
        "GBP", "GBR" -> resources.getString(R.string.gbr)
        "USD" -> resources.getString(R.string.usd)
        "EUR" -> resources.getString(R.string.eur)
        "KZT" -> resources.getString(R.string.kzt)
        "RUB" -> resources.getString(R.string.rub)
        "DKK" -> resources.getString(R.string.dkk)
        "INR" -> resources.getString(R.string.inr)
        "CAD" -> resources.getString(R.string.cad)
        "CNY" -> resources.getString(R.string.cny)
        "KRW" -> resources.getString(R.string.krw)
        "NOK" -> resources.getString(R.string.nok)
        "XDR" -> resources.getString(R.string.xdr)
        "SEK" -> resources.getString(R.string.sek)
        "CHF" -> resources.getString(R.string.chf)
        "JPY" -> resources.getString(R.string.jpy)
        "AMD" -> resources.getString(R.string.amd)
        "BYR" -> resources.getString(R.string.byr)
        "MDL" -> resources.getString(R.string.mdl)
        "TJS" -> resources.getString(R.string.tjs)
        "UZS" -> resources.getString(R.string.uzs)
        "UAH" -> resources.getString(R.string.uah)
        "KWD" -> resources.getString(R.string.kwd)
        "HUF" -> resources.getString(R.string.huf)
        "CZK" -> resources.getString(R.string.czk)
        "NZD" -> resources.getString(R.string.nzd)
        "PKR" -> resources.getString(R.string.pkr)
        "AUD" -> resources.getString(R.string.aud)
        "TRY" -> resources.getString(R.string.trytl)
        "AZN" -> resources.getString(R.string.azn)
        "SGD" -> resources.getString(R.string.sgd)
        "AFN" -> resources.getString(R.string.afn)
        "BGN" -> resources.getString(R.string.bgn)
        "BRL" -> resources.getString(R.string.brl)
        "GEL" -> resources.getString(R.string.gel)
        "AED" -> resources.getString(R.string.aed)
        "IRR" -> resources.getString(R.string.irr)
        "MYR" -> resources.getString(R.string.myr)
        "MNT" -> resources.getString(R.string.mnt)
        "TWD" -> resources.getString(R.string.twd)
        "TMT" -> resources.getString(R.string.tmt)
        "PLN" -> resources.getString(R.string.pln)
        "SAR" -> resources.getString(R.string.sar)
        "BYN" -> resources.getString(R.string.byn)
        else -> "None"
    }

    override fun onDetach() {
        if (!nbkrDisposable.isDisposed) {
            Log.d("DISPOSE", "NBKRFromSite disposed")
        } else {
            Log.d("DISPOSE", "NBKRFromSite already disposed")
        }

        super.onDetach()
    }

    override fun onDestroyView() {
        if (!nbkrDisposable.isDisposed) {
            Log.d("DISPOSE", "NBKRFromSite disposed")
        } else {
            Log.d("DISPOSE", "NBKRFromSite already disposed")
        }

        super.onDestroyView()
    }
}
