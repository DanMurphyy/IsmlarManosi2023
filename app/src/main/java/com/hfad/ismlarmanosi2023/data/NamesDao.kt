package com.hfad.ismlarmanosi2023.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NamesDao {
    @Query("SELECT * FROM names ORDER BY name ASC")
    fun getAllData(): Flow<List<NamesData>>

    @Query("SELECT * FROM names WHERE name LIKE:searchQuery")
    fun searchName(searchQuery: String): Flow<List<NamesData>>
}