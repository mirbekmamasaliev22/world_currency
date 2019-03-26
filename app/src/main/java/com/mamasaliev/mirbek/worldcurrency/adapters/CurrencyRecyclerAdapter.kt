package com.mamasaliev.mirbek.worldcurrency.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.mamasaliev.mirbek.worldcurrency.R
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.currency.view.*

class CurrencyRecyclerAdapter(val banks: ArrayList<BankRate>, private val context: Context):
    RecyclerView.Adapter<CurrencyViewHolder>() {

    private val publishSubject: PublishSubject<BankRate> = PublishSubject.create()

    override fun getItemCount(): Int {
        return this.banks.size
    }

    fun addItem(b: BankRate) {
        this.banks.add(b)
        this.notifyItemInserted(this.banks.size - 1)
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CurrencyViewHolder {
        return CurrencyViewHolder(LayoutInflater.from(context).inflate(R.layout.currency, parent, false))
    }

    fun onClickItemObservable(bank: Class<BankRate>):Observable<BankRate> {
        return publishSubject.ofType(bank)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(vh: CurrencyViewHolder, position: Int) {
        val bank = banks[position]

        vh.bankTitle.text = bank.bankInfo.name

        if (!bank.bankInfo.favicon.isEmpty())
            Picasso.get().load(bank.bankInfo.favicon).into(vh.favicon)


        RxView.clicks(vh.view).subscribe({
            publishSubject.onNext(bank)
        }, {error -> Log.d("error", "When clicking on bank item was error: ${error.message}")})

        var isEUR = false
        var isRUB = false
        var isKZT = false
        var isUSD = false

        for (currency in bank.currencies) {
            if (currency.name == "usd") {
                vh.usd_buy.text = currency.buy.toString()
                vh.usd_sell.text = currency.sell.toString()
                isUSD = true
            }

            if (currency.name == "eur") {
                vh.eur_buy.text = currency.buy.toString()
                vh.eur_sell.text = currency.sell.toString()
                isEUR = true
            }
            if (currency.name == "rub") {
                vh.rub_buy.text = currency.buy.toString()
                vh.rub_sell.text = currency.sell.toString()
                isRUB = true
            }
            if (currency.name == "kzt") {
                vh.kzt_buy.text = currency.buy.toString()
                vh.kzt_sell.text = currency.sell.toString()
                isKZT = true
            }
        }

        if (!isUSD) {
            vh.usd_buy.text = "0"
            vh.usd_sell.text = "0"
        }

        if (!isEUR) {
            vh.eur_buy.text = "0"
            vh.eur_sell.text = "0"
        }
        if (!isRUB) {
            vh.rub_buy.text = "0"
            vh.rub_sell.text = "0"
        }
        if (!isKZT) {
            vh.kzt_buy.text = "0"
            vh.kzt_sell.text = "0"
        }
    }
}

open class CurrencyViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val view = v
    val favicon = view.favicon!!
    val bankTitle = view.bankTitle!!
    val eur_buy = view.eur_buy!!
    val eur_sell= view.eur_sell!!
    val usd_buy = view.usd_buy!!
    val usd_sell= view.usd_sell!!
    val rub_buy = view.rub_buy!!
    val rub_sell= view.rub_sell!!
    val kzt_buy = view.kzt_buy!!
    val kzt_sell= view.kzt_sell!!
}