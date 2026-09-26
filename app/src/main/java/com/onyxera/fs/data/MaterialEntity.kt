package com.onyxera.fs.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * YENİ YAPI: "Çöp Kutusu" (Recycle Bin) mekanizması için is_deleted ve deleted_timestamp eklendi.
 * GÜNCELLEME: Excel'de gerçek para birimini gösterebilmek için "currency" sütunu eklendi.
 */
@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "ship_name") val shipName: String,
    @ColumnInfo(name = "material_name") val materialName: String,
    @ColumnInfo(name = "photo_uris") val photoUris: List<String>,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "received_date") val receivedDate: String,
    @ColumnInfo(name = "sent_date") val sentDate: String,
    @ColumnInfo(name = "price") val price: Double,

    // YENİ: Para birimi sembolü (Örn: TL, USD, EUR). Eski veriler çökmesin diye defaultValue eklendi.
    @ColumnInfo(name = "currency", defaultValue = "TL") val currency: String = "TL",

    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "width") val width: Double,
    @ColumnInfo(name = "height") val height: Double,
    @ColumnInfo(name = "length") val length: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long,

    // ÇÖP KUTUSU ALTYAPISI
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "deleted_timestamp") val deletedTimestamp: Long = 0L
)