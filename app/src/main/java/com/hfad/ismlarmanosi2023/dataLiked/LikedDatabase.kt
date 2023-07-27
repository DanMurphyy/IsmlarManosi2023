package com.hfad.ismlarmanosi2023.dataLiked

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [LikedData::class], version = 1, exportSchema = false)
abstract class LikedDatabase : RoomDatabase() {

    abstract fun likedDao(): LikedDao

    companion object {
        @Volatile
        private var INSTANCE: LikedDatabase? = null

        fun getDatabase(context: Context): LikedDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LikedDatabase::class.java,
                    "liked_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}