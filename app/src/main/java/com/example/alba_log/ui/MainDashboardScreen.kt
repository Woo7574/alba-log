package com.example.alba_log.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alba_log.navigation.AppScreen
import com.example.alba_log.ui.theme.Alba_logTheme

@Composable
fun MainDashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "알바 로그",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        DashboardButton(
            text = "스마트 인수인계",
            onClick = { navController.navigate(AppScreen.SmartHandover.route) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DashboardButton(
            text = "유통기한 관리자",
            onClick = { navController.navigate(AppScreen.ExpirationDateManager.route) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DashboardButton(
            text = "중앙 공지 시스템",
            onClick = { navController.navigate(AppScreen.CentralizedNotice.route) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DashboardButton(
            text = "근무 교대 도우미",
            onClick = { navController.navigate(AppScreen.ShiftSwapAssistant.route) }
        )
    }
}

@Composable
fun DashboardButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            // .fillMaxWidth() // 버튼 너비를 꽉 채우려면 주석 해제
            .padding(horizontal = 32.dp, vertical = 8.dp)
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainDashboardScreenPreview() {
    Alba_logTheme {
        MainDashboardScreen(navController = rememberNavController())
    }
}
