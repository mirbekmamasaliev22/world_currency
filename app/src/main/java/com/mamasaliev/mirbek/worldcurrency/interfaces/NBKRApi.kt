package com.mamasaliev.mirbek.worldcurrency.interfaces

import com.mamasaliev.mirbek.worldcurrency.entity.NBKRRate
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET


interface NBKRApi {

    @GET("XML/daily.xml")
    fun getDaily(): Observable<NBKRRate>

    @GET("XML/weekly.xml")
    fun getWeekly(): Observable<NBKRRate>

    companion object {
        fun create(): NBKRApi {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl("http://www.nbkr.kg")
                .build()
            return retrofit.create(NBKRApi::class.java)
        }
    }
}