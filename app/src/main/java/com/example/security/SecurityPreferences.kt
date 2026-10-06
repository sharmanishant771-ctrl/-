package com.example.security

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages access control PIN and security settings for the police directory app.
 * Ensures only authorized departmental personnel can view unmasked mobile numbers.
 */
class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("police_cctv_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_ADMIN_PIN_HASH = "admin_pin_hash"
        private const val KEY_SECURITY_ENABLED = "security_enabled"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ADMIN_ACTIVE = "is_admin_active"
        private const val KEY_LOGGED_MOBILE = "logged_mobile"
        private const val KEY_LOGGED_NAME = "logged_name"
        private const val KEY_LOGGED_DESIG = "logged_desig"
        private const val KEY_LOGGED_THANA = "logged_thana"
        // Master Admin Mobile Number (स्थायी मुख्य व्यवस्थापक / सुपर एडमिन)
        const val MASTER_ADMIN_NUMBER = "9807583096"
        // Default departmental authorization PIN is 1120
        const val DEFAULT_PIN = "1120"
        // Default Admin Master PIN is 8899 (केवल व्यवस्थापक हेतु)
        const val DEFAULT_ADMIN_PIN = "8899"
    }

    init {
        // Initialize with default departmental PIN if not set
        if (!prefs.contains(KEY_PIN_HASH)) {
            setPin(DEFAULT_PIN)
        }
        if (!prefs.contains(KEY_ADMIN_PIN_HASH)) {
            setAdminPin(DEFAULT_ADMIN_PIN)
        }
    }

    fun isMasterAdmin(mobile: String?): Boolean {
        if (mobile == null) return false
        val clean = mobile.filter { it.isDigit() }
        return clean == MASTER_ADMIN_NUMBER || (clean.length == 10 && clean == MASTER_ADMIN_NUMBER)
    }

    fun isAdminActive(): Boolean {
        val loggedMobile = prefs.getString(KEY_LOGGED_MOBILE, "") ?: ""
        if (isMasterAdmin(loggedMobile)) {
            return true
        }
        return prefs.getBoolean(KEY_IS_ADMIN_ACTIVE, false)
    }

    fun setAdminActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ADMIN_ACTIVE, active).apply()
    }

    fun verifyAdminPin(inputPin: String): Boolean {
        val storedHash = prefs.getString(KEY_ADMIN_PIN_HASH, "")
        return hashPin(inputPin) == storedHash
    }

    fun setAdminPin(newPin: String) {
        prefs.edit().putString(KEY_ADMIN_PIN_HASH, hashPin(newPin)).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(loggedIn: Boolean, mobile: String? = null, name: String? = null, designation: String? = null, thana: String? = null) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            .putString(KEY_LOGGED_MOBILE, mobile ?: "")
            .putString(KEY_LOGGED_NAME, name ?: "")
            .putString(KEY_LOGGED_DESIG, designation ?: "")
            .putString(KEY_LOGGED_THANA, thana ?: "")
            .apply()
    }

    fun getLoggedUser(): Map<String, String> {
        return mapOf(
            "mobile" to (prefs.getString(KEY_LOGGED_MOBILE, "") ?: ""),
            "name" to (prefs.getString(KEY_LOGGED_NAME, "") ?: ""),
            "designation" to (prefs.getString(KEY_LOGGED_DESIG, "") ?: ""),
            "thana" to (prefs.getString(KEY_LOGGED_THANA, "") ?: "")
        )
    }

    fun isSecurityEnabled(): Boolean {
        return prefs.getBoolean(KEY_SECURITY_ENABLED, true)
    }

    fun setSecurityEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SECURITY_ENABLED, enabled).apply()
    }

    fun verifyPin(inputPin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, "")
        return hashPin(inputPin) == storedHash
    }

    fun setPin(newPin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(newPin)).apply()
    }

    private fun hashPin(pin: String): String {
        return try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest((pin + "CCTV_SECURE_SALT_2026").toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            pin
        }
    }
}
