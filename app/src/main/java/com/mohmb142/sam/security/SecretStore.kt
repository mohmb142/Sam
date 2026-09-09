package com.mohmb142.sam.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecretStore(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "sam_secrets",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putApiKey(provider: String, key: String) {
        prefs.edit().putString("api_key_$provider", key).apply()
    }

    fun getApiKey(provider: String): String? = prefs.getString("api_key_$provider", null)

    fun removeApiKey(provider: String) {
        prefs.edit().remove("api_key_$provider").apply()
    }
}
