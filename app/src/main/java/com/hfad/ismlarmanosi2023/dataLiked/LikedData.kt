package com.hfad.ismlarmanosi2023.dataLiked

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "likedList")
data class LikedData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var gender: String,
    var meaning: String,
    var origin: String,
)