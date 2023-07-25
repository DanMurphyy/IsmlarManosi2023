package com.hfad.ismlarmanosi2023.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [NamesData::class], version = 1, exportSchema = false)
abstract class NamesDatabase : RoomDatabase() {
    abstract fun namesDao(): NamesDao
}

