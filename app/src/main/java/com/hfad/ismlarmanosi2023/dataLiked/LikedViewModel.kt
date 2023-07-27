package com.hfad.ismlarmanosi2023.dataLiked

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikedViewModel(application: Application) : AndroidViewModel(application) {
    private val likedDao = LikedDatabase.getDatabase(application).likedDao()
    private val repository: LikedRepository
    val getAllData: LiveData<List<LikedData>>

    init {
        repository = LikedRepository(likedDao)
        getAllData = repository.getAllData
    }

    fun insertData(likedData: LikedData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(likedData)
        }
    }

    fun deleteData(likedData: LikedData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(likedData)
        }
    }
}