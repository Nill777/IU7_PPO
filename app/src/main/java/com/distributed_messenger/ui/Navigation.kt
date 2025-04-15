package com.distributed_messenger.ui

import androidx.navigation.NavHostController

class NavigationController (private val navController: NavHostController) {
    // Маршруты
    private object Routes {
        const val AUTH = "auth"
        const val HOME = "home"
        const val PROFILE = "profile"
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

    fun navigateBack() {
        navController.popBackStack()
    }
}