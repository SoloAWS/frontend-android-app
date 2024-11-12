package com.misw.abcalls.data.api

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
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
        getSubFromToken(token)?.let { saveUserId(it) }
    }

    fun saveUserId(id: String) {
        sharedPreferences.edit()
            .putString("user_id", id)
            .apply()
    }

    private fun getSubFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val normalized = when (payload.length % 4) {
                0 -> payload
                1 -> payload.dropLast(1) + "==="
                2 -> payload + "=="
                3 -> payload + "="
                else -> payload
            }

            val decodedBytes = Base64.decode(normalized, Base64.URL_SAFE)
            val decodedString = String(decodedBytes)
            val jsonObject = JSONObject(decodedString)

            jsonObject.getString("sub")
        } catch (e: Exception) {
            null
        }
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