package com.doni.simling.helper.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.apply
import kotlin.collections.remove
import androidx.core.content.edit

@Singleton
class RoleManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val ROLE_KEY = "role"
        const val ROLE_ADMIN = 1
        const val ROLE_WARGA = 2
        const val ROLE_SECURITY = 3
    }

    fun saveRole(role: Int) {
        sharedPreferences.edit { putInt(ROLE_KEY, role) }
    }

    fun getRole(): Int? {
        val value = sharedPreferences.getInt(ROLE_KEY, -1)
        return if (value != -1) value else null
    }

    fun clearRole() {
        sharedPreferences.edit { remove(ROLE_KEY) }
    }

    fun isLoggedIn(): Boolean {
        return getRole() != null
    }
}