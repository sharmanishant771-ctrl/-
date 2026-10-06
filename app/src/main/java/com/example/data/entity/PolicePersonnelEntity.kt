package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for Room persistent storage.
 * Sensitive fields (mobile, cug, notes) are stored encrypted.
 */
@Entity(tableName = "police_personnel")
data class PolicePersonnelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val designation: String,
    val category: String,
    val thanaName: String,
    val encryptedMobileNumber: String,
    val encryptedCugNumber: String? = null,
    val dutyLocation: String? = null,
    val shiftTiming: String? = null,
    val email: String? = null,
    val isFavorite: Boolean = false,
    val encryptedNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
