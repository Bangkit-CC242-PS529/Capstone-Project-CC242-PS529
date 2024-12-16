package com.example.bookapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.bookapp.data.local.BookDatabase
import com.example.bookapp.domain.Book
import com.example.bookapp.domain.BookRepository
import com.example.bookapp.ui.navigation.BottomNavigationBar
import com.example.bookapp.ui.navigation.Routes
import com.example.bookapp.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ReadingListScreen(
    isConnected: Boolean,
    onNavigateToWord: () -> Unit,
    onNavigateToSoon: () -> Unit
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
    var readingList by remember { mutableStateOf<List<Book>>(emptyList()) }

    LaunchedEffect(true) {
        repo.getReadingList().collectLatest {
            readingList = it
        }
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8),
        bottomBar = {
            BottomNavigationBar(
                onWordClick = {
                    if (isConnected) onNavigateToWord() else showNoInternetPopup(context)
                },
                onReadingListClick = { /* current */ },
                onSoonClick = {
                    if (isConnected) onNavigateToSoon() else showNoInternetPopup(context)
                },
                currentRoute = Routes.ReadingList.route
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F4F8),
                            Color(0xFFE6EDF3)
                        )
                    )
                )
        ) {
            if (readingList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Empty Reading List",
                            modifier = Modifier.size(100.dp),
                        tint = Color(0xFF95A5A6)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Your reading list is empty",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xFF2C3E50),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Discover books by exploring our recommendations",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF7F8C8D)
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Reading List",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF2C3E50),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(readingList) { book ->
                            BookReadingListCard(
                                book = book,
                                onMarkAsRead = {
                                    coroutineScope.launch {
                                        repo.removeFromReadingList(book.ISBN)
                                        val count = withContext(Dispatchers.IO) { repo.countReadingList() }
                                        context.showToast("Congratulations on finishing ${book.title}! You have $count books left to read")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookReadingListCard(
    book: Book,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF2C3E50),
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    book.authors,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7F8C8D)
                    )
                )
                Text(
                    "ISBN: ${book.ISBN}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFA9A9A9)
                    )
                )
            }
            IconButton(
                onClick = onMarkAsRead,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFD4EFDF),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Mark as Read",
                    tint = Color(0xFF2ECC71)
                )
            }
        }
    }
}

fun showNoInternetPopup(context: android.content.Context) {
    Toast.makeText(context, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show()
}