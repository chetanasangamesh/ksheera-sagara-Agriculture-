package com.example.ksheera_sagara.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MilkEntry::class, Expense::class],
    version = 1,
    exportSchema = false
)
abstract class DairyDatabase : RoomDatabase() {

    abstract fun dairyDao(): DairyDao

    companion object {
        @Volatile
        private var INSTANCE: DairyDatabase? = null

        fun getDatabase(context: Context): DairyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DairyDatabase::class.java,
                    "ksheera_sagara_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
