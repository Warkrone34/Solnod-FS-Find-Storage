package com.onyxera.fs.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShip(ship: ShipEntity): Long

    @Update
    suspend fun updateShip(ship: ShipEntity)

    @Delete
    suspend fun deleteShip(ship: ShipEntity)

    @Query("SELECT * FROM ships ORDER BY name ASC")
    fun getAllShips(): Flow<List<ShipEntity>>

    @Query("SELECT * FROM ships ORDER BY name ASC")
    suspend fun getShipsList(): List<ShipEntity>

    @Query("SELECT COUNT(*) FROM ships")
    suspend fun getShipCount(): Int

    @Query("DELETE FROM ships WHERE id = :id")
    suspend fun deleteShipById(id: Int)
}
