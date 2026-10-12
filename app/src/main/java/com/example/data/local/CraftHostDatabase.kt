package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Main Room Database for CraftHost local persistence.
 */
@Database(
    entities = [ServerEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class CraftHostDatabase : RoomDatabase() {

    abstract fun serverDao(): MinecraftServerDao

    companion object {
        @Volatile
        private var INSTANCE: CraftHostDatabase? = null

        fun getDatabase(context: Context): CraftHostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CraftHostDatabase::class.java,
                    "crafthost_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
