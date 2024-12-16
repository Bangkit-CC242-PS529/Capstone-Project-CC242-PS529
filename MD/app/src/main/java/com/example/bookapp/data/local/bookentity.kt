package com.example.bookapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_list")
data class BookEntity(
    @PrimaryKey val ISBN: String,
    val authors: String,
    val title: String
)
