package com.hfad.ismlarmanosi2023.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class NamesViewModel @Inject constructor(
    private val namesRepository: NamesRepository,
) : ViewModel() {
    val getAllData = namesRepository.getAllData

    fun searchName(searchQuery: String): Flow<List<NamesData>> {
        return namesRepository.searchName(searchQuery)
    }
}