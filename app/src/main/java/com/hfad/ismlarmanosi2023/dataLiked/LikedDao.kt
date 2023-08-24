package com.hfad.ismlarmanosi2023.dataLiked

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LikedDao {

    @Query("SELECT * FROM likedList ORDER by id ASC")
    fun getAllData(): LiveData<List<LikedData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(likedData: LikedData)

    @Delete
    fun deleteData(likedData: LikedData)
}