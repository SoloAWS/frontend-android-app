package com.misw.abcalls.utils

import android.content.Context
import java.util.Locale

class LocaleManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
    private val SELECTED_LOCALE_KEY = "selected_locale"

    fun getSelectedLocale(): Locale {
        // Check if the user has previously selected a locale
        val localeString = sharedPreferences.getString(SELECTED_LOCALE_KEY, null)
        if (localeString != null) {
            return Locale.forLanguageTag(localeString)
        }

        // If no locale is saved, use the system's default locale
        return Locale.getDefault()
    }

    fun setSelectedLocale(locale: Locale) {
        // Save the selected locale to shared preferences
        sharedPreferences.edit().putString(SELECTED_LOCALE_KEY, locale.toLanguageTag()).apply()
        updateConfiguration(locale)
    }

    private fun updateConfiguration(locale: Locale) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}

