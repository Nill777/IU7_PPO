package com.distributed_messenger.ui

import androidx.navigation.NavHostController

class NavigationController (private val navController: NavHostController) {
    // Маршруты
    private object Routes {
        const val AUTH = "auth"
        const val HOME = "home"
        const val PROFILE = "profile"
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

    fun navigateToProfile() {
        navController.navigate(Routes.PROFILE)
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