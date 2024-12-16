package com.example.bookapp.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.bookapp.data.local.BookDatabase
import com.example.bookapp.domain.Book
import com.example.bookapp.domain.BookRepository
import com.example.bookapp.domain.Resource
import com.example.bookapp.ui.navigation.BottomNavigationBar
import com.example.bookapp.ui.navigation.Routes
import com.example.bookapp.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    isConnected: Boolean,
    onNavigateToReadingList: () -> Unit,
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

    var text by remember { mutableStateOf(TextFieldValue("")) }
    var bookList by remember { mutableStateOf<List<Book>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val verticalOffset by animateDpAsState(
        targetValue = if (bookList.isNotEmpty()) 0.dp else 200.dp,
        animationSpec = spring()
    )
    val coroutineScope = rememberCoroutineScope()

    fun showNoInternetPopup() {
        Toast.makeText(context, "You don't have internet. Try again later.", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8),
        bottomBar = {
            BottomNavigationBar(
                onWordClick = { /* Current Screen: do nothing */ },
                onReadingListClick = {
                    if (isConnected) onNavigateToReadingList() else showNoInternetPopup()
                },
                onSoonClick = {
                    if (isConnected) onNavigateToSoon() else showNoInternetPopup()
                },
                currentRoute = Routes.Word.route
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Spacer(modifier = Modifier.height(verticalOffset))

                Text(
                    "Think of a Word",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF2C3E50),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter a word", color = Color(0xFF2980B9)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3498DB),
                        unfocusedBorderColor = Color(0xFF95A5A6),
                        cursorColor = Color(0xFF3498DB)
                    ),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isConnected) {
                            showNoInternetPopup()
                            return@Button
                        }
                        coroutineScope.launch {
                            loading = true
                            errorMessage = null
                            bookList = emptyList()
                            val response = withContext(Dispatchers.IO) {
                                repo.getRecommendations(text.text, 5)
                            }
                            loading = false
                            when (response) {
                                is Resource.Success -> {
                                    bookList = response.data ?: emptyList()
                                }
                                is Resource.Error -> {
                                    val msg = response.message ?: "Unknown error"
                                    context.showToast(msg)
                                }
                                else -> {}
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3498DB),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Get Recommendations", fontSize = 16.sp)
                }

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(50.dp),
                        color = Color(0xFF3498DB)
                    )
                }

                if (bookList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
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

@Composable
fun BookRecommendationCard(
    book: Book,
    onSave: (Book) -> Unit,
    onOpen: (Book) -> Unit
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
            Row {
                IconButton(
                    onClick = { onOpen(book) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFECF0F1),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Open Book",
                        tint = Color(0xFF2980B9)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onSave(book) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFECF0F1),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Save Book",
                        tint = Color(0xFFE74C3C)
                    )
                }
            }
        }
    }
}