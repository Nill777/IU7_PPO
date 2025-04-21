package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.ui.NavigationController

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    navigationController: NavigationController
) {
    Column {
        Button(onClick = { navigationController.navigateToUserManagement() }) {
            Text("Управление ролями")
        }
        Button(onClick = { navigationController.navigateToBlockManagement() }) {
            Text("Блокировка пользователей")
        }
        Button(onClick = { navigationController.navigateToAppSettings() }) {
            Text("Настройки приложения")
        }
    }
}