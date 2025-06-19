package com.cduffaut.swiftycompanion

import android.content.Context
import android.content.SharedPreferences
import com.cduffaut.swiftycompanion.api.ApiClient
import com.cduffaut.swiftycompanion.model.Token

// gestionaire du token d'authentification OAuth
object TokenManager {
    private const val PREF_NAME = "SwiftyCompanionPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_EXPIRES_AT = "expires_at"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: Token) {
        // calcul date d'exp depuis timestamp actuel + durée token
        val expiresAt = System.currentTimeMillis() + (token.expiresIn * 1000)

        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token.accessToken)
            .putString(KEY_REFRESH_TOKEN, token.refreshToken)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()

        // m à j du token dans ApiClient pour futures requêtes
        ApiClient.setToken(token.accessToken)
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // check if token is expired to not create a token for each query
    // but only if needed
    fun isTokenExpired(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0)
        return System.currentTimeMillis() > expiresAt
    }

    fun hasToken(): Boolean {
        return getAccessToken() != null
    }

    fun clearToken() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_EXPIRES_AT)
            .apply()

        ApiClient.setToken(null)
    }
}