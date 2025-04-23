package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.presenter.viewmodels.AuthViewModel.AuthState
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.core.logging.LogLevel
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.ui.NavigationController

@Composable
fun AuthScreen(viewModel: AuthViewModel,
               navigationController: NavigationController) {
    // Подписываемся на "статус" от ViewModel
    // collectAsState() превращает Flow в "живое состояние", которое автоматически обновляет UI
    val authState by viewModel.authState.collectAsState()
    // Поля ввода
    // remember — "запоминание" значения между обновлениями UI
    // mutableStateOf — "магнитная доска", изменения на которой автоматически обновляют экран
    var username by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.RegistrationSuccess -> {
                Logger.log("AuthScreen", "Registration success")
                navigationController.navigateToProfile()
            }
            is AuthState.LoginSuccess -> {
                Logger.log("AuthScreen", "Login success")
                navigationController.navigateToProfile()
            }
            is AuthState.Error -> {
                Logger.log("AuthScreen", "Auth error: ${(authState as AuthState.Error).message}", LogLevel.ERROR)
            }
            else -> {}
        }
    }

    Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        Button(
            onClick = {
                Logger.log("AuthScreen", "Register attempt: $username")
                viewModel.register(username, UserRole.ADMINISTRATOR)
            },
            enabled = authState !is AuthState.Loading
        ) {
            Text("Register")
        }

        Button(
            onClick = {
                Logger.log("AuthScreen", "Login attempt: $username")
                viewModel.login(username)
            },
            enabled = authState !is AuthState.Loading
        ) {
            Text("Login")
        }
    
        // Обработка состояний
        when (authState) {
            // Unit — это аналог void в Kotlin, но как объект
            AuthState.Loading -> {
                Logger.log("AuthScreen", "Loading state")
                CircularProgressIndicator() // спиннер загрузки, как вращающийся значок
            }
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red
            )
            else -> {}
        }
    }
}