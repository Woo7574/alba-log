package com.example.alba_log

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alba_log.expiration_date_manager.ExpirationDateManagerScreen // 추가
import com.example.alba_log.navigation.AppScreen
import com.example.alba_log.smart_handover.SmartHandoverScreen
import com.example.alba_log.ui.MainDashboardScreen
import com.example.alba_log.ui.theme.Alba_logTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Alba_logTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlbaLogNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AlbaLogNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.MainDashboard.route,
        modifier = modifier
    ) {
        composable(AppScreen.MainDashboard.route) {
            MainDashboardScreen(navController = navController)
        }
        composable(AppScreen.SmartHandover.route) {
            SmartHandoverScreen(navController = navController)
        }
        composable(AppScreen.ExpirationDateManager.route) {
            ExpirationDateManagerScreen(navController = navController) // 변경
        }
        composable(AppScreen.CentralizedNotice.route) {
            PlaceholderScreen("중앙 공지 시스템 화면")
        }
        composable(AppScreen.ShiftSwapAssistant.route) {
            PlaceholderScreen("근무 교대 도우미 화면")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Alba_logTheme {
        AlbaLogNavigation()
    }
}
