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

    private fun getLanguageCode(): String {
        return preference.getString(PREFERENCE_LANGUAGE, "uz") ?: "uz"
    }

    private val _quoteData = MutableLiveData<List<Quotes>>()
    val quoteData: LiveData<List<Quotes>> get() = _quoteData

    init {
        getStartQuoteData()
    }

    private fun getStartQuoteData() {
        val lang = getLanguageCode()
        _quoteData.value = QuoteManager.getQuotes(lang)
    }

}