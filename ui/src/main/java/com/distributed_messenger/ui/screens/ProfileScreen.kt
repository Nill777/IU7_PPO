package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
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
            is ProfileViewModel.ProfileState.Loading -> CircularProgressIndicator() // Показать индикатор загрузки
            is ProfileViewModel.ProfileState.Error -> Text(
                text = (state as ProfileViewModel.ProfileState.Error).message,
                color = MaterialTheme.colorScheme.error // Показать сообщение об ошибке
            )
            else -> {
                if (currentUser?.role == UserRole.ADMINISTRATOR) {
                    Button(
                        onClick = { navigationController.navigateToAdminDashboard() }
                    ) {
                        Text("Админ-панель")
                    }
                }
            }
        }
    }
}

