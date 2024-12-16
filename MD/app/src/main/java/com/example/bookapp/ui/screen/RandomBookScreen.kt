package com.example.bookapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.bookapp.data.local.BookDatabase
import com.example.bookapp.domain.Book
import com.example.bookapp.domain.BookRepository
import com.example.bookapp.ui.navigation.BottomNavigationBar
import com.example.bookapp.ui.navigation.Routes
import com.example.bookapp.utils.showToast
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookapp.domain.Resource
import com.example.bookapp.ui.screens.BookRecommendationCard
import kotlinx.coroutines.launch

@Composable
fun RandomBooksScreen(
    isConnected: Boolean,
    onNavigateToWord: () -> Unit,
    onNavigateToReadingList: () -> Unit
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            "books.db"
        ).build()
    }
    val repo = remember { BookRepository(db.bookDao()) }
    val coroutineScope = rememberCoroutineScope()

    var bookList by remember { mutableStateOf<List<Book>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onWordClick = { if (isConnected) onNavigateToWord() else context.showToast("No internet connection.") },
                onReadingListClick = { if (isConnected) onNavigateToReadingList() else context.showToast("No internet connection.") },
                onSoonClick = { /* Current Screen */ },
                currentRoute = Routes.Soon.route
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!isConnected) {
                            context.showToast("No internet connection.")
                            return@Button
                        }
                        coroutineScope.launch {
                            loading = true
                            errorMessage = null
                            val response = repo.getRandomRecommendations()
                            loading = false
                            when (response) {
                                is Resource.Success -> {
                                    bookList = response.data ?: emptyList()
                                }
                                is Resource.Error -> {
                                    errorMessage = response.message
                                    context.showToast(errorMessage ?: "Unknown error.")
                                }
                                else -> {}
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3498DB),
                        contentColor = Color.White
                    )
                ) {
                    Text("Generate Your Recommendation")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                }

                if (bookList.isNotEmpty()) {
                    LazyColumn {
                        items(bookList) { book ->
                            BookRecommendationCard(
                                book = book,
                                onSave = { savedBook ->
                                    coroutineScope.launch {
                                        repo.saveToReadingList(savedBook)
                                        context.showToast("Saved to Reading List")
                                    }
                                },
                                onOpen = { openBook ->
                                    val query = "${openBook.title} by ${openBook.authors}"
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/search?q=" + Uri.encode(query))
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
