package com.example.bookapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM reading_list")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: BookEntity)

    @Query("DELETE FROM reading_list WHERE ISBN = :isbn")
    suspend fun deleteByIsbn(isbn: String)

    @Query("SELECT COUNT(*) FROM reading_list")
    suspend fun countBooks(): Int
}
