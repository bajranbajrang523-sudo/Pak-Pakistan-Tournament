package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pro_genius_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "logged_user_id"
        private const val KEY_USER_ROLE = "logged_user_role"
    }

    fun saveSession(userId: Long, role: String) {
        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_ROLE, role)
            .apply()
    }

    fun getLoggedUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1L)
        return if (id != -1L) id else null
    }

    fun getLoggedUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
