package com.distributed_messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.presenter.viewmodels.AuthViewModel.AuthState
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.LogLevel
import com.distributed_messenger.logger.Logger
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
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.RegistrationSuccess -> {
                Logger.log("AuthScreen", "Registration success")
                navigationController.navigateToChatList()
            }
            is AuthState.LoginSuccess -> {
                Logger.log("AuthScreen", "Login success")
                navigationController.navigateToChatList()
            }
            is AuthState.Error -> {
                Logger.log("AuthScreen", "Auth error: ${(authState as AuthState.Error).message}", LogLevel.ERROR)
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
//            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
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

            // Поле ввода
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .background(colorScheme.background)
            )

            // Кнопка входа
            Button(
                onClick = { viewModel.login(username) },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Кнопка регистрации
            Button(
                onClick = { viewModel.register(username, UserRole.ADMINISTRATOR) },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Register",
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}