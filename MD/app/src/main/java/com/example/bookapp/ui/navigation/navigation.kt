package com.example.bookapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookapp.ui.screen.RandomBooksScreen
import com.example.bookapp.ui.screens.ReadingListScreen
import com.example.bookapp.ui.screens.SplashScreen
import com.example.bookapp.ui.screens.WordScreen

sealed class Routes(val route: String) {
    object Splash: Routes("splash")
    object Word: Routes("word")
    object ReadingList: Routes("reading_list")
    object Soon: Routes("soon")
}

@Composable
fun AppNavHost(navController: NavHostController, isConnected: Boolean) {
    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(onSplashDone = {
                navController.navigate(Routes.Word.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Routes.Word.route) {
            WordScreen(
                isConnected = isConnected,
                onNavigateToReadingList = {
                    navController.navigate(Routes.ReadingList.route)
                },
                onNavigateToSoon = {
                    navController.navigate(Routes.Soon.route)
                }
            )
        }
        composable(Routes.ReadingList.route) {
            ReadingListScreen(
                isConnected = isConnected,
                onNavigateToWord = { navController.navigate(Routes.Word.route) },
                onNavigateToSoon = { navController.navigate(Routes.Soon.route) }
            )
        }
        composable(Routes.Soon.route) {
            RandomBooksScreen (
                isConnected = isConnected,
                onNavigateToWord = { navController.navigate(Routes.Word.route) },
                onNavigateToReadingList = { navController.navigate(Routes.ReadingList.route) }
            )
        }
    }
}
