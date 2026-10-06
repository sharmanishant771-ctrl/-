package com.example.data.repository

import com.example.data.dao.PersonnelDao
import com.example.data.db.SampleData
import com.example.data.entity.PolicePersonnelEntity
import com.example.data.firestore.FirestoreSyncService
import com.example.data.model.Personnel
import com.example.data.model.PersonnelCategory
import com.example.security.CryptoManager
import com.example.util.CallHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonnelRepository(
    private val dao: PersonnelDao,
    private val firestoreSync: FirestoreSyncService? = null
) {

    val allPersonnel: Flow<List<Personnel>> = dao.getAllPersonnel().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val allThanaNames: Flow<List<String>> = dao.getAllThanaNames()

    suspend fun insert(personnel: Personnel): Long {
        val id = dao.insert(personnel.toEntity())
        val saved = personnel.copy(id = id)
        firestoreSync?.saveToCloud(saved)
        return id
    }

    suspend fun insertBulk(list: List<Personnel>): List<Long> {
        val entities = list.map { it.toEntity() }
        val ids = dao.insertAll(entities)
        firestoreSync?.saveBulkToCloud(list)
        return ids
    }

    suspend fun update(personnel: Personnel) {
        dao.update(personnel.toEntity())
        firestoreSync?.saveToCloud(personnel)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
        firestoreSync?.deleteFromCloud(id)
    }

    suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
        val entity = dao.getPersonnelById(id)
        if (entity != null) {
            firestoreSync?.saveToCloud(entity.toDomainModel())
        }
    }

    suspend fun resetToSampleData() {
        dao.deleteAll()
        dao.insertAll(SampleData.getInitialPersonnel())
        firestoreSync?.saveBulkToCloud(SampleData.getInitialPersonnel().map { it.toDomainModel() })
    }

    suspend fun checkAndSeedInitialData() {
        val currentEntities = dao.getAllPersonnelList()
        val hasMasterAdmin = currentEntities.any {
            val num = CryptoManager.decrypt(it.encryptedMobileNumber)
            num.filter { c -> c.isDigit() }.endsWith(com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER)
        }
        val hasAmethiData = currentEntities.any {
            it.thanaName.contains("गौरीगंज") || it.thanaName.contains("अमेठी") || it.thanaName.contains("मुसाफिरखाना")
        }

        if (currentEntities.isEmpty() || !hasAmethiData) {
            dao.deleteAll()
            dao.insertAll(SampleData.getInitialPersonnel())
        } else if (!hasMasterAdmin) {
            dao.insert(SampleData.getMasterAdminEntity())
        }

        // Sync with Cloud Firestore
        firestoreSync?.syncFromCloud()

        // Double check after sync: Guarantee master admin is present in local database!
        val afterSync = dao.getAllPersonnelList()
        val hasAdminAfter = afterSync.any {
            val num = CryptoManager.decrypt(it.encryptedMobileNumber)
            num.filter { c -> c.isDigit() }.endsWith(com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER)
        }
        if (!hasAdminAfter) {
            val insertedId = dao.insert(SampleData.getMasterAdminEntity())
            firestoreSync?.saveToCloud(SampleData.getMasterAdminPersonnel().copy(id = insertedId))
        }
    }

    suspend fun syncWithCloud() {
        firestoreSync?.syncFromCloud()
    }

    suspend fun findPersonnelByMobile(rawMobile: String): Personnel? {
        val clean = CallHelper.cleanPhoneNumber(rawMobile)

        // Priority 1: Direct Master Admin check (9807583096)
        if (clean == com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER ||
            clean.endsWith(com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER)) {
            val list = dao.getAllPersonnelList()
            for (entity in list) {
                val decryptedMobile = CryptoManager.decrypt(entity.encryptedMobileNumber)
                val cleanDecrypted = CallHelper.cleanPhoneNumber(decryptedMobile)
                if (cleanDecrypted == com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER ||
                    cleanDecrypted.endsWith(com.example.security.SecurityPreferences.MASTER_ADMIN_NUMBER)) {
                    return entity.toDomainModel()
                }
            }
            // Master admin wasn't in DB yet -> immediately insert into DB, save to cloud, and return!
            val adminEntity = SampleData.getMasterAdminEntity()
            val insertedId = dao.insert(adminEntity)
            val adminPersonnel = SampleData.getMasterAdminPersonnel().copy(id = insertedId)
            firestoreSync?.saveToCloud(adminPersonnel)
            return adminPersonnel
        }

        // Priority 2: General personnel search
        val list = dao.getAllPersonnelList()
        for (entity in list) {
            val decryptedMobile = CryptoManager.decrypt(entity.encryptedMobileNumber)
            val cleanDecrypted = CallHelper.cleanPhoneNumber(decryptedMobile)
            if (cleanDecrypted == clean || (clean.length == 10 && cleanDecrypted.endsWith(clean))) {
                return entity.toDomainModel()
            }
        }
        return null
    }

    private fun PolicePersonnelEntity.toDomainModel(): Personnel {
        return Personnel(
            id = id,
            name = name,
            designation = designation,
            category = PersonnelCategory.fromString(category),
            thanaName = thanaName,
            mobileNumber = CryptoManager.decrypt(encryptedMobileNumber),
            cugNumber = CryptoManager.decrypt(encryptedCugNumber),
            dutyLocation = dutyLocation ?: "",
            shiftTiming = shiftTiming ?: "",
            email = email ?: "",
            isFavorite = isFavorite,
            notes = CryptoManager.decrypt(encryptedNotes),
            createdAt = createdAt
        )
    }

    private fun Personnel.toEntity(): PolicePersonnelEntity {
        return PolicePersonnelEntity(
            id = id,
            name = name.trim(),
            designation = designation.trim(),
            category = category.name,
            thanaName = thanaName.trim(),
            encryptedMobileNumber = CryptoManager.encrypt(mobileNumber.trim()),
            encryptedCugNumber = if (cugNumber.isNotBlank()) CryptoManager.encrypt(cugNumber.trim()) else null,
            dutyLocation = dutyLocation.trim().ifBlank { null },
            shiftTiming = shiftTiming.trim().ifBlank { null },
            email = email.trim().ifBlank { null },
            isFavorite = isFavorite,
            encryptedNotes = if (notes.isNotBlank()) CryptoManager.encrypt(notes.trim()) else null,
            createdAt = if (createdAt == 0L) System.currentTimeMillis() else createdAt
        )
    }
}
