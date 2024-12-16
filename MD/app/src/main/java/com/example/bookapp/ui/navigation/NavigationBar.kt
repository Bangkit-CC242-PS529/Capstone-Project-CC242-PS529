package com.example.bookapp.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookapp.R

@Composable
fun BottomNavigationBar(
    onWordClick: () -> Unit,
    onReadingListClick: () -> Unit,
    onSoonClick: () -> Unit,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, clip = true)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF0F4F8),
                        Color(0xFFE6EDF3)
                    )
                )
            )
    ) {
        val iconTint = Color(0xFF2C3E50)
        val selectedTint = Color(0xFF3498DB)

        NavigationBarItem(
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.word_idea),
                        contentDescription = "Word",
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRoute == Routes.Word.route) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(20.dp)
                                .background(selectedTint, shape = MaterialTheme.shapes.small)
                        )
                    }
                }
            },
            selected = currentRoute == Routes.Word.route,
            onClick = { onWordClick() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedTint,
                unselectedIconColor = iconTint
            )
        )

        NavigationBarItem(
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.read_list),
                        contentDescription = "Reading List",
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRoute == Routes.ReadingList.route) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(20.dp)
                                .background(selectedTint, shape = MaterialTheme.shapes.small)
                        )
                    }
                }
            },
            selected = currentRoute == Routes.ReadingList.route,
            onClick = { onReadingListClick() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedTint,
                unselectedIconColor = iconTint
            )
        )

        NavigationBarItem(
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.soon_icon),
                        contentDescription = "Soon",
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRoute == Routes.Soon.route) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(20.dp)
                                .background(selectedTint, shape = MaterialTheme.shapes.small)
                        )
                    }
                }
            },
            selected = currentRoute == Routes.Soon.route,
            onClick = { onSoonClick() },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedTint,
                unselectedIconColor = iconTint
            )
        )
    }
}