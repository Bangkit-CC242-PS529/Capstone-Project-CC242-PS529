package com.example.bookapp.domain

import com.example.bookapp.data.local.BookDao
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.remote.NetworkClient
import com.example.bookapp.data.remote.RecommendRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(private val dao: BookDao) {

    fun getReadingList(): Flow<List<Book>> {
        return dao.getAllBooks().map { list ->
            list.map { Book(it.ISBN, it.authors, it.title) }
        }
    }

    suspend fun saveToReadingList(book: Book) {
        dao.insertBook(
            BookEntity(
                ISBN = book.ISBN,
                authors = book.authors,
                title = book.title
            )
        )
    }

    suspend fun removeFromReadingList(isbn: String) {
        dao.deleteByIsbn(isbn)
    }

    suspend fun countReadingList(): Int {
        return dao.countBooks()
    }

    suspend fun getRecommendations(title: String, k: Int = 5): Resource<List<Book>> {
        return try {
            val response = NetworkClient.wordRecommendationApi.getRecommendations(RecommendRequest(title, k))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.recommendations != null && body.recommendations.isNotEmpty()) {
                    val books = body.recommendations.map {
                        Book(it.ISBN ?: "N/A", it.Authors ?: "Unknown", it.Titles ?: "Untitled")
                    }
                    Resource.Success(books)
                } else {
                    Resource.Error("No recommendations found.")
                }
            } else {
                Resource.Error("Error: ${response.errorBody()?.string() ?: "Unknown"}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun getRandomRecommendations(): Resource<List<Book>> {
        return try {
            val response = NetworkClient.randomRecommendationApi.getRandomRecommendations()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.recommended_books != null && body.recommended_books.isNotEmpty()) {
                    val books = body.recommended_books.map {
                        Book(it.ISBN ?: "N/A", it.Authors ?: "Unknown", it.Titles ?: "Untitled")
                    }
                    Resource.Success(books)
                } else {
                    Resource.Error("No recommendations found.")
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

}
