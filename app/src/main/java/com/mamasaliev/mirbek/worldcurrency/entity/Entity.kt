package com.mamasaliev.mirbek.worldcurrency.entity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "CurrencyRates", strict = false)
class NBKRRate(
    @field: Attribute(name = "Name", required=false)
    @param: Attribute(name = "Name", required=false)
    var name: String = "",
    @field: Attribute(name = "Date", required=false)
    @param: Attribute(name = "Date", required=false)
    var date: String = "",
    @field: ElementList(entry="Currency", name="Currency", inline=true, required = false)
    @param: ElementList(entry="Currency", name="Currency", inline=true, required = false)
    var currencies: List<NBKRCurrency>
) {

    init {
        Log.d("xml1_init", "${this.name}, ${this.date}")
    }
}

@Root(name = "Currency", strict = false)
class NBKRCurrency(
    @field: Attribute(name = "ISOCode", required=false)
    @param: Attribute(name = "ISOCode", required=false)
    var code: String = "",
    @field: Element(name = "Nominal", required=false)
    @param: Element(name = "Nominal", required=false)
    var nominal: String = "",
    @field: Element(name = "Value", required=false)
    @param: Element(name = "Value", required=false)
    var value: String = ""
) {

    init {
        Log.d("xml2_init", "${this.code}, ${this.nominal}, ${this.value}")
    }
}


@Entity(tableName = "central_bank_currency", primaryKeys = ["code"])
data class SimpleCurrency(
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "nominal") val nominal: String = "",
    @ColumnInfo(name = "change_rate") val changeRate: String = "")

data class BankInfo(val id: Int,
                    val name: String,
                    val name_ru: String,
                    val name_kg: String,
                    val site_url: String,
                    val favicon: String,
                    val table_selector: String,
                    val currency_name: String,
                    val currency_buy: String,
                    val currency_sell: String): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(name)
        dest?.writeString(name_ru)
        dest?.writeString(name_kg)
        dest?.writeString(site_url)
        dest?.writeString(favicon)
        dest?.writeString(table_selector)
        dest?.writeString(currency_name)
        dest?.writeString(currency_buy)
        dest?.writeString(currency_sell)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BankInfo> {
        override fun createFromParcel(parcel: Parcel): BankInfo {
            return BankInfo(parcel)
        }

        override fun newArray(size: Int): Array<BankInfo?> {
            return arrayOfNulls(size)
        }
    }

}

data class BankRate(val bankInfo: BankInfo, val currencies: ArrayList<BankCurrency>):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BankInfo::class.java.classLoader)!!,
        arrayListOf<BankCurrency>().apply {
            parcel.readArrayList(BankCurrency::class.java.classLoader)
        }
    )

    fun getCurrency(currencyName: String):BankCurrency? {
        for (currency in currencies) {
            if (currency.name == currencyName.toLowerCase().trim().substring(0,3)) {
                return currency
            }
        }
        return null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(bankInfo, flags)
        parcel.writeList(currencies)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BankRate> {
        override fun createFromParcel(parcel: Parcel): BankRate {
            return BankRate(parcel)
        }

        override fun newArray(size: Int): Array<BankRate?> {
            return arrayOfNulls(size)
        }
    }
}

@SuppressLint("ParcelCreator")
@Entity(tableName = "bank_currency", primaryKeys = ["bank_id", "name"])
data class BankCurrency(@ColumnInfo(name = "bank_id") var bank_id: Int,
                        @ColumnInfo(name = "name") var name: String,
                        @ColumnInfo(name = "buy") var buy: Double,
                        @ColumnInfo(name = "sell") var sell: Double):Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bank_id)
        parcel.writeString(name)
        parcel.writeDouble(buy)
        parcel.writeDouble(sell)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<BankCurrency> {
        override fun createFromParcel(parcel: Parcel): BankCurrency {
            return BankCurrency(parcel)
        }

        override fun newArray(size: Int): Array<BankCurrency?> {
            return arrayOfNulls(size)
        }
    }
}