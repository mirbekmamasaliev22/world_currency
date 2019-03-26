package com.mamasaliev.mirbek.worldcurrency

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.mamasaliev.mirbek.worldcurrency.entity.BankRate
import com.mamasaliev.mirbek.worldcurrency.fragments.*


class MainActivity : AppCompatActivity(), CurrencyFragment.OnCurrencyFragmentListener {

    val banks = ArrayList<BankRate>()
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    private val currencyFragmentTag = "CURRENCY_FRAGMENT"
    private val nbkrFragmentTag = "NBKR_FRAGMENT"
    private val settingsFragmentTag = "SETTINGS_FRAGMENT"
    private val aboutFragmentTag = "ABOUT_FRAGMENT"

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewElements()
        initToolbar()

        val currencyFragment = CurrencyFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.content_frame, currencyFragment, currencyFragmentTag)
            .commit()

        navigationDrawerItemClickListener()
    }

    private fun navigationDrawerItemClickListener() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.currency -> {
                    var currencyFragment = supportFragmentManager.findFragmentByTag(currencyFragmentTag)
                    if (currencyFragment == null) {
                        currencyFragment = CurrencyFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, currencyFragment, currencyFragmentTag)
                        .addToBackStack(null)
                        .commit()

                }
                R.id.nbkr -> {
                    var nbkrFragment = supportFragmentManager.findFragmentByTag(nbkrFragmentTag)
                    if (nbkrFragment == null) {
                        nbkrFragment = CentralBankFragment()
                    }
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, nbkrFragment, nbkrFragmentTag)
                        .addToBackStack(null)
                        .commit()
                }
                /*R.id.settings -> {
                    var settingsFragment = supportFragmentManager.findFragmentByTag(settingsFragmentTag)
                    if (settingsFragment == null) {
                        settingsFragment = SettingsFragment()
                    }
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, settingsFragment, settingsFragmentTag)
                        .addToBackStack(null)
                        .commit()
                }*/
                R.id.info -> {
                    var aboutFragment = supportFragmentManager.findFragmentByTag(aboutFragmentTag)
                    if (aboutFragment == null) {
                        aboutFragment = AboutFragment()
                    }
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.content_frame, aboutFragment, aboutFragmentTag)
                        .addToBackStack(null)
                        .commit()
                }
            }

            true
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_hamburger)
        }
    }

    private fun initViewElements() {
        mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.nav_view)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                Log.d("click", "Hamburger clicked")
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBankSelected(currencyName:String, isBuy: Boolean, bank: BankRate, banks: ArrayList<BankRate>) {
        val converterFragment = ConverterFragment.newInstance(currencyName, isBuy, banks, banks.indexOf(bank))
        converterFragment.show(supportFragmentManager, "converterFragment")
    }
}