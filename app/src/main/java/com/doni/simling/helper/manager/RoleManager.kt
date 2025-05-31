package com.doni.simling.helper.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val ROLE_KEY = "role"
        const val ROLE_ADMIN = "admin"
        const val ROLE_WARGA = "warga"
        const val ROLE_SECURITY = "security"
    }

    fun saveRole(role: String) {
        sharedPreferences.edit().putString(ROLE_KEY, role).apply()
    }

    fun getRole(): String? {
        return sharedPreferences.getString(ROLE_KEY, null)
    }

    fun clearRole() {
        sharedPreferences.edit().remove(ROLE_KEY).apply()
    }

    fun isLoggedIn(): Boolean {
        return !getRole().isNullOrEmpty()
    }
}