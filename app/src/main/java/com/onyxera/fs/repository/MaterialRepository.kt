package com.onyxera.fs.repository

import com.onyxera.fs.data.MaterialDao
import com.onyxera.fs.data.MaterialEntity
import com.onyxera.fs.data.ShipDao
import com.onyxera.fs.data.ShipEntity
import kotlinx.coroutines.flow.Flow

class MaterialRepository(
    private val materialDao: MaterialDao,
    private val shipDao: ShipDao
) {

    val activeMaterials: Flow<List<MaterialEntity>> = materialDao.getActiveMaterials()
    val deletedMaterials: Flow<List<MaterialEntity>> = materialDao.getDeletedMaterials()
    val allShips: Flow<List<ShipEntity>> = shipDao.getAllShips()

    suspend fun insert(material: MaterialEntity) = materialDao.insertMaterial(material)
    suspend fun update(material: MaterialEntity) = materialDao.updateMaterial(material)

    // Kalıcı Silme
    suspend fun deletePermanently(material: MaterialEntity) = materialDao.deleteMaterial(material)

    // Çöpe Taşıma
    suspend fun moveToTrash(ids: List<Int>) {
        materialDao.moveMaterialsToTrash(ids, System.currentTimeMillis())
    }

    // Çöpten Çıkarma
    suspend fun restoreFromTrash(ids: List<Int>) {
        materialDao.restoreMaterialsFromTrash(ids)
    }

    // Zaman Aşımına Uğrayanları Silme (15 Gün)
    suspend fun clearOldTrash(threshold: Long) {
        materialDao.cleanOldTrashPermanently(threshold)
    }

    // Gemi / Proje İşlemleri
    suspend fun insertShip(ship: ShipEntity): Long = shipDao.insertShip(ship)
    suspend fun updateShip(ship: ShipEntity) = shipDao.updateShip(ship)
    suspend fun deleteShip(ship: ShipEntity) = shipDao.deleteShip(ship)
    suspend fun deleteShipById(id: Int) = shipDao.deleteShipById(id)
    suspend fun getShipsList(): List<ShipEntity> = shipDao.getShipsList()
    suspend fun getShipCount(): Int = shipDao.getShipCount()
}