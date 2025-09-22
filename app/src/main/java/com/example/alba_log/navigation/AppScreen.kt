package com.example.alba_log.navigation

sealed class AppScreen(val route: String) {
    object MainDashboard : AppScreen("main_dashboard_screen")
    object SmartHandover : AppScreen("smart_handover_screen")
    object ExpirationDateManager : AppScreen("expiration_date_manager_screen")
    object CentralizedNotice : AppScreen("centralized_notice_screen")
    object ShiftSwapAssistant : AppScreen("shift_swap_assistant_screen")
    // TODO: 스마트 인수인계 확인 화면 경로 추가 예정
    // object HandoverConfirmation : AppScreen("handover_confirmation_screen")
}