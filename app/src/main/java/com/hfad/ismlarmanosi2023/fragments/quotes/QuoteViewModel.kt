package com.hfad.ismlarmanosi2023.fragments.quotes

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.danmurphyy.quotes.QuoteManager
import com.danmurphyy.quotes.Quotes
import com.hfad.ismlarmanosi2023.language.PREFERENCE_LANGUAGE
import com.hfad.ismlarmanosi2023.language.PREFERENCE_NAME

class QuoteViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val preference: SharedPreferences =
        application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private fun getLoginCount(): String? {
        return preference.getString(PREFERENCE_LANGUAGE, "uz")
    }

    private val _quoteData = MutableLiveData<List<Quotes>>()
    val quoteData: LiveData<List<Quotes>> get() = _quoteData

    init {
        getStartQuoteData()
    }

    private fun getStartQuoteData() {
        val lang = getLoginCount()

        val quotes = when (lang) {
            "uz" -> QuoteManager.startQuoteUz
            "ru" -> QuoteManager.startQuoteRu
            "en" -> QuoteManager.startQuoteEn
            else -> QuoteManager.startQuoteEn
        }

        _quoteData.value = quotes
    }

}