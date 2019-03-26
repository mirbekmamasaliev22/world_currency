package com.mamasaliev.mirbek.worldcurrency.interfaces

import android.arch.persistence.room.*
import com.mamasaliev.mirbek.worldcurrency.entity.BankCurrency
import com.mamasaliev.mirbek.worldcurrency.entity.SimpleCurrency

@Dao
interface AppDao {
    @Query("SELECT * FROM bank_currency WHERE bank_id = :bank_id")
    fun getBankCurrencyRate(bank_id: Int): List<BankCurrency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBankCurrencies(bankCurrencies: ArrayList<BankCurrency>)

    @Delete
    fun deleteBankCurrency(bankCurrency: BankCurrency)

    @Query("SELECT * FROM central_bank_currency")
    fun getCentralBankCurrencyRate(): List<SimpleCurrency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCentralBankCurrencies(simpleCurrency: ArrayList<SimpleCurrency>)

    @Delete
    fun deleteCentralBankCurrency(simpleCurrency: SimpleCurrency)
}