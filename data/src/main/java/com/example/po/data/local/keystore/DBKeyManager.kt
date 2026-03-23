package com.example.po.data.local.keystore

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import android.util.Base64

class DBKeyManager(private val context: Context) {
    private val PREFS_NAME = "po_secure_prefs"
    private val KEY_DB_PASSPHRASE = "db_passphrase"

    private val sharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getDatabaseKey(): ByteArray {
        val encodedKey = sharedPreferences.getString(KEY_DB_PASSPHRASE, null)
        return if (encodedKey != null) {
            Base64.decode(encodedKey, Base64.DEFAULT)
        } else {
            val secureRandom = SecureRandom()
            val newKey = ByteArray(32)
            secureRandom.nextBytes(newKey)
            val newEncodedKey = Base64.encodeToString(newKey, Base64.DEFAULT)
            sharedPreferences.edit().putString(KEY_DB_PASSPHRASE, newEncodedKey).apply()
            newKey
        }
    }
}
