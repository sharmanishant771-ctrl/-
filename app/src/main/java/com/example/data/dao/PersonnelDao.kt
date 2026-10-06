package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.PolicePersonnelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonnelDao {
    @Query("SELECT * FROM police_personnel ORDER BY isFavorite DESC, thanaName ASC, name ASC")
    fun getAllPersonnel(): Flow<List<PolicePersonnelEntity>>

    @Query("SELECT * FROM police_personnel")
    suspend fun getAllPersonnelList(): List<PolicePersonnelEntity>

    @Query("SELECT * FROM police_personnel WHERE id = :id LIMIT 1")
    suspend fun getPersonnelById(id: Long): PolicePersonnelEntity?

    @Query("SELECT DISTINCT thanaName FROM police_personnel ORDER BY thanaName ASC")
    fun getAllThanaNames(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM police_personnel")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PolicePersonnelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PolicePersonnelEntity>): List<Long>

    @Update
    suspend fun update(entity: PolicePersonnelEntity)

    @Delete
    suspend fun delete(entity: PolicePersonnelEntity)

    @Query("DELETE FROM police_personnel WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE police_personnel SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM police_personnel")
    suspend fun deleteAll()
}
