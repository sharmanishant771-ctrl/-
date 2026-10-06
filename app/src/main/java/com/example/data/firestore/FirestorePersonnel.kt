package com.example.data.firestore

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirestorePersonnel(
    var id: String = "",
    var localId: Long = 0L,
    var name: String = "",
    var designation: String = "",
    var category: String = "CCTV_OPERATOR",
    var thanaName: String = "",
    var mobileNumber: String = "",
    var cugNumber: String = "",
    var dutyLocation: String = "",
    var shiftTiming: String = "",
    var email: String = "",
    var isFavorite: Boolean = false,
    var notes: String = "",
    var createdAt: Long = 0L
)
