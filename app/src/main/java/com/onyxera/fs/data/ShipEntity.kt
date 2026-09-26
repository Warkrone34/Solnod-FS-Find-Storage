package com.onyxera.fs.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcı tarafından tanımlanan Gemi ve Proje Bilgileri.
 * Sabit isimler yerine kullanıcıya tam özgürlük sunar.
 */
@Entity(tableName = "ships")
data class ShipEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "details")
    val details: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
