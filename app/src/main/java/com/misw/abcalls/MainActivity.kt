package com.misw.abcalls

import Navigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.misw.abcalls.ui.theme.AbcallsTheme
import com.misw.abcalls.utils.LocaleManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val localeManager by lazy { LocaleManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val locale = localeManager.getSelectedLocale()
            updateLocale(locale)
            AbcallsTheme {
                Navigation()
            }
        }
    }

    private fun updateLocale(locale: Locale) {
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}