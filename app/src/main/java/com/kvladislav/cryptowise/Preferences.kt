package com.kvladislav.cryptowise

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kvladislav.cryptowise.models.CurrencySetWrapper

class Preferences(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)


    fun setStringPrefs(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getStringPrefs(key: String): String? = sharedPreferences.getString(key, null)


    fun setFavouriteCurrencies(ids: CurrencySetWrapper) {
        val jsonString = Gson().toJson(ids)
        setStringPrefs(FAVOURITE_CURRENCIES_KEY, jsonString)
    }

    fun clearFavouriteCurrencies() {
        setStringPrefs(FAVOURITE_CURRENCIES_KEY, null)
    }

    fun getFavouriteCurrencies(): CurrencySetWrapper {
        val rawString = getStringPrefs(FAVOURITE_CURRENCIES_KEY)
        return rawString?.let {
            Gson().fromJson(it, CurrencySetWrapper::class.java)
        } ?: CurrencySetWrapper()
    }

    companion object {
        const val SHARED_PREFS_FILE = "com.kvladislav.cryptowise.shared_prefs"
        const val FAVOURITE_CURRENCIES_KEY = "FAVOURITE_CURRENCIES_KEY"
    }
}