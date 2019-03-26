package com.mamasaliev.mirbek.worldcurrency.settings

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.mamasaliev.mirbek.worldcurrency.helper.AppDatabase

class AppCurrency: Application() {

    companion object {
        lateinit var appDatabase: AppDatabase
        lateinit var appContext: Context

        fun isInternetConnected(): Boolean {
            val connectivityManager = AppCurrency.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppCurrency", "onCreate")

        AppCurrency.appContext = applicationContext

        AppCurrency.appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "world_currency"
        ).build()
    }

}