package com.onyxera.fs.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Solnod F&S Ana Yerel Veritabanı (Room Database).
 * Gemi & Proje yönetimi (ShipEntity) eklendiği için versiyon 5'e yükseltildi.
 */
@Database(
    entities = [MaterialEntity::class, ShipEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FSDatabase : RoomDatabase() {

    abstract fun materialDao(): MaterialDao
    abstract fun shipDao(): ShipDao

    companion object {
        @Volatile
        private var INSTANCE: FSDatabase? = null

        fun getDatabase(context: Context): FSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FSDatabase::class.java,
                    "solnod_fs_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}