package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.dao.PersonnelDao
import com.example.data.db.SampleData
import com.example.data.entity.PolicePersonnelEntity
import com.example.data.model.Personnel
import com.example.security.CryptoManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(
    private val context: Context,
    private val dao: PersonnelDao
) {
    private val TAG = "FirestoreSync"

    val db: FirebaseFirestore by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firestore with custom DB ID, fallback", e)
            FirebaseFirestore.getInstance()
        }
    }

    private val collectionRef by lazy {
        db.collection("district_personnel")
    }

    /**
     * Pulls latest contacts from Cloud Firestore into local encrypted storage.
     * Ensures all contacts return even if phone is changed!
     */
    suspend fun syncFromCloud() {
        try {
            val snapshot = collectionRef.get().await()
            if (!snapshot.isEmpty) {
                val cloudEntities = snapshot.documents.mapNotNull { doc ->
                    val data = doc.toObject(FirestorePersonnel::class.java) ?: return@mapNotNull null
                    PolicePersonnelEntity(
                        id = if (data.localId > 0) data.localId else 0L,
                        name = data.name,
                        designation = data.designation,
                        category = data.category,
                        thanaName = data.thanaName,
                        encryptedMobileNumber = CryptoManager.encrypt(data.mobileNumber),
                        encryptedCugNumber = if (data.cugNumber.isNotBlank()) CryptoManager.encrypt(data.cugNumber) else null,
                        dutyLocation = data.dutyLocation.ifBlank { null },
                        shiftTiming = data.shiftTiming.ifBlank { null },
                        email = data.email.ifBlank { null },
                        isFavorite = data.isFavorite,
                        encryptedNotes = if (data.notes.isNotBlank()) CryptoManager.encrypt(data.notes) else null,
                        createdAt = if (data.createdAt > 0) data.createdAt else System.currentTimeMillis()
                    )
                }

                if (cloudEntities.isNotEmpty()) {
                    val hasAmethi = cloudEntities.any {
                        it.thanaName.contains("गौरीगंज") || it.thanaName.contains("अमेठी") || it.thanaName.contains("मुसाफिरखाना")
                    }
                    val hasAdmin = cloudEntities.any {
                        val mobile = CryptoManager.decrypt(it.encryptedMobileNumber)
                        mobile.filter { c -> c.isDigit() }.endsWith(com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER)
                    }

                    if (!hasAmethi) {
                        Log.d(TAG, "Cloud data is from older district, updating with Amethi CCTNS directory...")
                        seedCloudWithInitialData()
                        dao.deleteAll()
                        dao.insertAll(SampleData.getInitialPersonnel())
                    } else {
                        val finalEntities = cloudEntities.toMutableList()
                        if (!hasAdmin) {
                            Log.d(TAG, "Master admin not found in cloud, adding master admin...")
                            val adminEntity = SampleData.getMasterAdminEntity()
                            finalEntities.add(0, adminEntity)
                            saveToCloud(SampleData.getMasterAdminPersonnel())
                        }
                        dao.deleteAll()
                        dao.insertAll(finalEntities)
                        Log.d(TAG, "Synced ${finalEntities.size} contacts from Cloud (including Admin)")
                    }
                }
            } else {
                // If cloud is totally empty on initial launch, upload sample data to cloud
                seedCloudWithInitialData()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Cloud sync failed (offline or network issue): ${e.message}")
        }
    }

    /**
     * Seeds initial district police contacts into Cloud Firestore
     */
    private suspend fun seedCloudWithInitialData() {
        try {
            val samples = SampleData.getInitialPersonnel()
            for ((idx, entity) in samples.withIndex()) {
                val docId = "police_${idx + 1}"
                val firestoreObj = FirestorePersonnel(
                    id = docId,
                    localId = (idx + 1).toLong(),
                    name = entity.name,
                    designation = entity.designation,
                    category = entity.category,
                    thanaName = entity.thanaName,
                    mobileNumber = CryptoManager.decrypt(entity.encryptedMobileNumber),
                    cugNumber = CryptoManager.decrypt(entity.encryptedCugNumber),
                    dutyLocation = entity.dutyLocation ?: "",
                    shiftTiming = entity.shiftTiming ?: "",
                    email = entity.email ?: "",
                    isFavorite = entity.isFavorite,
                    notes = CryptoManager.decrypt(entity.encryptedNotes),
                    createdAt = entity.createdAt
                )
                collectionRef.document(docId).set(firestoreObj)
            }
            Log.d(TAG, "Seeded ${samples.size} contacts to Cloud Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to seed Cloud Firestore: ${e.message}")
        }
    }

    /**
     * Uploads or updates a contact to Cloud Firestore
     */
    fun saveToCloud(personnel: Personnel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val docId = if (personnel.id > 0) "police_${personnel.id}" else "police_${System.currentTimeMillis()}"
                val firestoreObj = FirestorePersonnel(
                    id = docId,
                    localId = personnel.id,
                    name = personnel.name,
                    designation = personnel.designation,
                    category = personnel.category.name,
                    thanaName = personnel.thanaName,
                    mobileNumber = personnel.mobileNumber,
                    cugNumber = personnel.cugNumber,
                    dutyLocation = personnel.dutyLocation,
                    shiftTiming = personnel.shiftTiming,
                    email = personnel.email,
                    isFavorite = personnel.isFavorite,
                    notes = personnel.notes,
                    createdAt = personnel.createdAt
                )
                collectionRef.document(docId).set(firestoreObj).await()
                Log.d(TAG, "Saved contact to Cloud: ${personnel.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save contact to Cloud: ${e.message}")
            }
        }
    }

    /**
     * Deletes a contact from Cloud Firestore
     */
    fun deleteFromCloud(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val docId = "police_$id"
                collectionRef.document(docId).delete().await()
                Log.d(TAG, "Deleted contact from Cloud: $docId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete from Cloud: ${e.message}")
            }
        }
    }

    /**
     * Uploads bulk contacts to Cloud Firestore
     */
    fun saveBulkToCloud(list: List<Personnel>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (p in list) {
                    val docId = "police_${System.currentTimeMillis()}_${p.mobileNumber}"
                    val firestoreObj = FirestorePersonnel(
                        id = docId,
                        localId = p.id,
                        name = p.name,
                        designation = p.designation,
                        category = p.category.name,
                        thanaName = p.thanaName,
                        mobileNumber = p.mobileNumber,
                        cugNumber = p.cugNumber,
                        dutyLocation = p.dutyLocation,
                        shiftTiming = p.shiftTiming,
                        email = p.email,
                        isFavorite = p.isFavorite,
                        notes = p.notes,
                        createdAt = p.createdAt
                    )
                    collectionRef.document(docId).set(firestoreObj)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save bulk to Cloud: ${e.message}")
            }
        }
    }
}
