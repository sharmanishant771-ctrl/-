package com.example.data.model

import com.example.security.CryptoManager

/**
 * Domain model representing a police personnel or CCTV operator.
 */
data class Personnel(
    val id: Long = 0,
    val name: String,
    val designation: String,
    val category: PersonnelCategory,
    val thanaName: String,
    val mobileNumber: String,
    val cugNumber: String = "",
    val dutyLocation: String = "",
    val shiftTiming: String = "",
    val email: String = "",
    val isFavorite: Boolean = false,
    val notes: String = "",
    val createdAt: Long = 0L
) {
    /**
     * Returns masked phone number if locked or unauthorized
     */
    fun getDisplayMobile(isUnlocked: Boolean): String {
        return if (isUnlocked) mobileNumber else CryptoManager.maskPhoneNumber(mobileNumber)
    }

    /**
     * Returns masked CUG number if locked or unauthorized
     */
    fun getDisplayCug(isUnlocked: Boolean): String {
        if (cugNumber.isBlank()) return ""
        return if (isUnlocked) cugNumber else CryptoManager.maskPhoneNumber(cugNumber)
    }
}
