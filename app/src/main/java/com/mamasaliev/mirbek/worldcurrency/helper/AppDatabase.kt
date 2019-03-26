package com.mamasaliev.mirbek.worldcurrency.helper

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.mamasaliev.mirbek.worldcurrency.entity.BankCurrency
import com.mamasaliev.mirbek.worldcurrency.entity.SimpleCurrency
import com.mamasaliev.mirbek.worldcurrency.interfaces.AppDao


@Database(entities = [BankCurrency::class, SimpleCurrency::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
}