package com.misw.abcalls.data.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "auth_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .putLong("token_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun saveUserId(id: String) {
        sharedPreferences.edit()
            .putString("user_id", id)
            .apply()
    }

    fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        val timestamp = sharedPreferences.getLong("token_timestamp", 0)
        if (token != null && System.currentTimeMillis() - timestamp > 24 * 60 * 60 * 1000) {
            clearToken()
            return null
        }

        return token
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }
}