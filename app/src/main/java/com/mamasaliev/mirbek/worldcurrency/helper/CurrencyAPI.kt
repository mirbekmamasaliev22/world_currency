package com.mamasaliev.mirbek.worldcurrency.helper

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.google.gson.Gson
import com.mamasaliev.mirbek.worldcurrency.entity.BankCurrency
import com.mamasaliev.mirbek.worldcurrency.entity.BankInfo
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import com.mamasaliev.mirbek.worldcurrency.settings.AppCurrency
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.reactivestreams.Subscriber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeoutException


open class CurrencyAPI {

    @SuppressLint("CheckResult")
    fun allBanks(observer: Subscriber<BankRate>) {

        val banksInfo = readJSON()


        Flowable.create(FlowableOnSubscribe<BankRate> { subscriber ->
            for (bank in banksInfo) {
                if (subscriber.isCancelled) { break }
                try {
                    val bankRate = if (AppCurrency.isInternetConnected()) getBankCurrencyFromInternet(bank) else getBankCurrencyFromDatabase(bank)

                    Log.d("Flowable:", "onSend: ${bank.id}")
                    subscriber.onNext(bankRate)
                }catch (e: TimeoutException) {
                    Log.d("Flowable:", "onFloableError: ${e.message}")
                }
            }
            subscriber.onComplete()

        }, BackpressureStrategy.BUFFER)
            .parallel(5)
            .runOn(Schedulers.io())
            .map { d ->
                Log.d("Flowable", "Threan name: ${Thread.currentThread().name}, id = ${d.bankInfo.id}")
                d }
            .sequential()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun getBankCurrencyFromDatabase(bankInfo: BankInfo): BankRate {
        val currenciesList = AppCurrency.appDatabase.appDao().getBankCurrencyRate(bankInfo.id)
        val currenciesArrayList = ArrayList<BankCurrency>()
        for (currency in currenciesList) currenciesArrayList.add(currency)
        return BankRate(bankInfo, currenciesArrayList)
    }

    private fun getBankCurrencyFromInternet(bankInfo: BankInfo): BankRate {

        val doc = Jsoup.connect(bankInfo.site_url).get()
        val rows = doc.body().select(bankInfo.table_selector)

        System.out.println(rows.size)
        val currencies = ArrayList<BankCurrency>()
        for (row in rows) {
            var name = row.select(bankInfo.currency_name).text().toLowerCase().trim().substring(0,3)
            var buy = row.select(bankInfo.currency_buy).text().replace(",", ".")
            var sell = row.select(bankInfo.currency_sell).text().replace(",", ".")

            buy = if (buy.indexOf(" ") > 0) buy.substring(0, buy.indexOf(" ")) else buy
            sell = if (sell.indexOf(" ") > 0) sell.substring(0, sell.indexOf(" ")) else sell

            if (name == "rur") name = "rub"
            val bankCurrency = BankCurrency(bankInfo.id, name, buy = buy.toDouble(), sell = sell.toDouble())

            currencies.add(bankCurrency)
        }
        AppCurrency.appDatabase.appDao().insertBankCurrencies(currencies)

        return BankRate(bankInfo, currencies)
    }

    public fun readJSON(): Array<BankInfo> {
        val gson = Gson()
        val br = BufferedReader(InputStreamReader(javaClass.getResourceAsStream("/assets/data.json")))
        val banks = gson.fromJson(br, Array<BankInfo>::class.java)
        br.close()
        return banks
    }
}
