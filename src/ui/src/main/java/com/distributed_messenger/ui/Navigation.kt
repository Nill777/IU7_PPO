package com.distributed_messenger.ui

import androidx.navigation.NavHostController
import java.util.UUID

class NavigationController (private val navController: NavHostController) {
    // Маршруты
    private object Routes {
        const val AUTH = "auth"
        const val CHAT_LIST = "chat_list"
        const val CHAT = "chat/{chatId}"
        const val MESSAGE_HISTORY = "message_history/{messageId}"
        const val NEW_CHAT = "new_chat"
        const val HOME = "home"
        const val PROFILE = "profile"
        const val SETTINGS = "settings"
        const val ABOUT_PROGRAM = "about_program"
        const val ADMIN_DASHBOARD = "admin_dashboard"
        const val USER_MANAGEMENT = "role_management"
        const val BLOCK_MANAGEMENT = "block_management"
        const val APP_SETTINGS = "app_settings"
    }

    // Методы навигации
    fun navigateToAuth() {
        navController.navigate(Routes.AUTH)
    }

    fun navigateToHome() {
        navController.navigate(Routes.HOME)
    }

    fun navigateToChatList() {
        navController.navigate(Routes.CHAT_LIST)
    }

    fun navigateToChat(chatId: UUID) {
        navController.navigate("chat/$chatId")
    }

    fun navigateToMessageHistory(messageId: UUID) {
        navController.navigate("message_history/$messageId")
    }

    fun navigateToNewChat() {
        navController.navigate(Routes.NEW_CHAT)
    }

    fun navigateToProfile() {
        navController.navigate(Routes.PROFILE)
    }

    fun navigateToSettings() {
        navController.navigate(Routes.SETTINGS)
    }

    fun navigateToAboutProgram() {
        navController.navigate(Routes.ABOUT_PROGRAM)
    }

    fun navigateToAdminDashboard() {
        navController.navigate(Routes.ADMIN_DASHBOARD)
    }

    fun navigateToUserManagement() {
        navController.navigate(Routes.USER_MANAGEMENT)
    }

    fun navigateToBlockManagement() {
        navController.navigate(Routes.BLOCK_MANAGEMENT)
    }

    fun navigateToAppSettings() {
        navController.navigate(Routes.APP_SETTINGS)
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}