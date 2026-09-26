package com.onyxera.fs.data

import androidx.room.TypeConverter

/**
 * Room Veritabanı Tip Dönüştürücü Sınıfı.
 * SQLite standart olarak List (Dizi) yapılarını desteklemez.
 * Bu sınıf, çoklu fotoğraf URI'lerini veritabanına yazarken tek bir metne dönüştürür,
 * okurken de tekrar List<String> objesine ayrıştırır.
 */
class Converters {

    @TypeConverter
    fun fromUriList(uris: List<String>?): String {
        // Güvenli ayraç (|||) kullanılarak virgül içeren dosya yollarında çökme engellenir.
        return uris?.joinToString(separator = "|||") ?: ""
    }

    @TypeConverter
    fun toUriList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split("|||")
    }
}