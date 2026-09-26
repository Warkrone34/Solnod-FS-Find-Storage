package com.onyxera.fs.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    // GERÇEK SİLME İŞLEMİ (Kalıcı olarak yok eder)
    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)

    // ANA LİSTE: Sadece silinmemiş (is_deleted = 0) öğeleri gösterir.
    @Query("SELECT * FROM materials WHERE is_deleted = 0 ORDER BY timestamp DESC")
    fun getActiveMaterials(): Flow<List<MaterialEntity>>

    // ÇÖP KUTUSU LİSTESİ: Sadece silinmiş öğeleri silinme tarihine göre gösterir.
    @Query("SELECT * FROM materials WHERE is_deleted = 1 ORDER BY deleted_timestamp DESC")
    fun getDeletedMaterials(): Flow<List<MaterialEntity>>

    // YUMUŞAK SİLME (Soft Delete): Öğeyi çöpe taşır.
    @Query("UPDATE materials SET is_deleted = 1, deleted_timestamp = :deleteTime WHERE id IN (:ids)")
    suspend fun moveMaterialsToTrash(ids: List<Int>, deleteTime: Long)

    // KURTARMA: Öğeyi çöpten ana listeye geri alır.
    @Query("UPDATE materials SET is_deleted = 0, deleted_timestamp = 0 WHERE id IN (:ids)")
    suspend fun restoreMaterialsFromTrash(ids: List<Int>)

    // 15 GÜNLÜK OTOMATİK TEMİZLİK: Verilen süreden önce çöpe atılmış her şeyi kalıcı olarak siler.
    @Query("DELETE FROM materials WHERE is_deleted = 1 AND deleted_timestamp < :thresholdTime")
    suspend fun cleanOldTrashPermanently(thresholdTime: Long)
}