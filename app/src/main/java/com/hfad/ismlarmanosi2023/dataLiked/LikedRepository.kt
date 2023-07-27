package com.hfad.ismlarmanosi2023.dataLiked

import androidx.lifecycle.LiveData

class LikedRepository(private val likedDao: LikedDao) {

    val getAllData: LiveData<List<LikedData>> = likedDao.getAllData()

    fun insertData(likedData: LikedData) {
        likedDao.insertData(likedData)
    }

    fun deleteData(likedData: LikedData) {
        likedDao.deleteData(likedData)
    }
}