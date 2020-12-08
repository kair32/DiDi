package com.aks.didi.utils

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


interface Preference{
    fun setCookie(cookie: String?)
    fun getCookie(): String?

    fun firstDataSuccessful(isSuccessful: Boolean)
    fun isFirstDataSuccessful(): Boolean
}

class PreferencesBasket(context: Context): Preference {
    private val preferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()

        val masterKey: MasterKey = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build()

        EncryptedSharedPreferences.create(
            context,
            NAME_PREF,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }
    else context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)

    override fun setCookie(cookie: String?) = preferences.edit().putString(KEY_COOKIE, cookie).apply()
    override fun getCookie(): String? = preferences.getString(KEY_COOKIE, null)

    override fun firstDataSuccessful(isSuccessful: Boolean) = preferences.edit().putBoolean(KEY_FIRST_DATA_SUCCESSFUL, isSuccessful).apply()
    override fun isFirstDataSuccessful(): Boolean = preferences.getBoolean(KEY_FIRST_DATA_SUCCESSFUL, false)

    companion object{
        private const val NAME_PREF = "PreferencesBasket"
        private const val KEY_COOKIE = "PREF_KEY_COOKIE"
        private const val KEY_FIRST_DATA_SUCCESSFUL = "PREF_KEY_FIRST_DATA_SUCCESSFUL"
    }
}