package com.doni.simling.helper.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(private val sharedPref: SharedPreferences) {

    companion object {
        private const val TOKEN_KEY = "auth_token"
    }

    fun saveToken(token: String) {
        sharedPref.edit { putString(TOKEN_KEY, token) }
    }

    fun getToken(): String? {
        return sharedPref.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        sharedPref.edit { remove(TOKEN_KEY) }
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}