package com.mamasaliev.mirbek.worldcurrency.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.mamasaliev.mirbek.worldcurrency.R


class SettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        val spinnerLanguage: Spinner = v.findViewById(R.id.spnrLang)

        val languages = listOf("Kyrgyz", "Russian", "English")

        // Spinner
        val spinnerAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, languages)
        spinnerLanguage.adapter = spinnerAdapter


        return v
    }
}
