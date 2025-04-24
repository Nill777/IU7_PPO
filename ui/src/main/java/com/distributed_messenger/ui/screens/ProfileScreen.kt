package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.LogLevel
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.presenter.viewmodels.ProfileViewModel
import com.distributed_messenger.ui.NavigationController
import java.util.UUID

@Composable
fun ProfileScreen(viewModel: ProfileViewModel,
                  navigationController: NavigationController) {
    val currentUser by viewModel.user.collectAsState()
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        Logger.log("ProfileScreen", "Initial user load started")
        viewModel.loadCurrentUser() // Загружаем текущего пользователя при первом запуске
    }
    
    Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium
        )
    
        when (state) {
            is ProfileViewModel.ProfileState.Loading -> {
                Logger.log("ProfileScreen", "Loading state active")
                CircularProgressIndicator() // Показать индикатор загрузки
            }
            is ProfileViewModel.ProfileState.Error -> {
                val errorMsg = (state as ProfileViewModel.ProfileState.Error).message
                Logger.log("ProfileScreen", "Error: $errorMsg", LogLevel.ERROR)
                Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                Logger.log("ProfileScreen", "User data loaded: ${currentUser?.id}")
                if (currentUser?.role == UserRole.ADMINISTRATOR) {
                    Button(
                        onClick = {
                            Logger.log("ProfileScreen", "Navigating to admin dashboard")
                            navigationController.navigateToAdminDashboard()
                        }
                    ) {
                        Text("Админ-панель")
                    }
                }
            }
        }
    }
}