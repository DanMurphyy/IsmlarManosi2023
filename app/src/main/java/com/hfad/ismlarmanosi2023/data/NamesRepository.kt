package com.hfad.ismlarmanosi2023.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NamesRepository @Inject constructor(
    private val namesDao: NamesDao,
) {
    val getAllData = namesDao.getAllData()

    fun searchName(searchQuery: String): Flow<List<NamesData>> {
        return namesDao.searchName(searchQuery)
    }
}