package example.bookapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.bookapp.ui.navigation.AppNavHost
import com.example.bookapp.utils.ConnectivityObserverImpl

@Composable
fun BookApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserverImpl(context) }
    val status by connectivityObserver.connectivityState.collectAsState(initial = true)

    // status = true means connected, false means disconnected

    AppNavHost(navController = navController, isConnected = status)
}
