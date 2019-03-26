package com.mamasaliev.mirbek.worldcurrency.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mamasaliev.mirbek.worldcurrency.R
import com.mamasaliev.mirbek.worldcurrency.entity.SimpleCurrency
import kotlinx.android.synthetic.main.nbkr_currency_item.view.*

class NBKRCurrencyAdapter(private val currencies: ArrayList<SimpleCurrency>,
                          val context: Context):
    RecyclerView.Adapter<NBKRCurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NBKRCurrencyHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.nbkr_currency_item, parent, false)
        return NBKRCurrencyHolder(v)
    }

    override fun getItemCount(): Int = currencies.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NBKRCurrencyHolder, position: Int) {
        val c = currencies[position]
        holder.cName.text = c.name
        holder.cValue.text = "${c.nominal} ${c.code} = ${c.value} SOM"
        holder.cRate.text = c.changeRate
        holder.cCode.text = c.code
    }
}

class NBKRCurrencyHolder(v: View): RecyclerView.ViewHolder(v) {
    val view = v
    val cName = v.cname!!
    val cValue = v.cvalue!!
    val cRate = v.cRate!!
    val cCode = v.cCode!!
}