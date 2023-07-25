package com.hfad.ismlarmanosi2023.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "names")
@Parcelize
data class NamesData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gender: String,
    val meaning: String,
    val origin: String,
    ): Parcelable
