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
import com.distributed_messenger.ui.NavigationController

// ui/screens/AuthScreen.kt
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    navigationController: NavigationController
) {
    // Подписываемся на "статус" от ViewModel
    // collectAsState() превращает Flow в "живое состояние", которое автоматически обновляет UI
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Поля ввода
        // remember — "запоминание" значения между обновлениями UI
        // mutableStateOf — "магнитная доска", изменения на которой автоматически обновляют экран
        var username by remember { mutableStateOf("") }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        // Кнопки действий
        Button(
            onClick = { viewModel.register(username, UserRole.ADMINISTRATOR) },
            enabled = authState !is AuthState.Loading
        ) {
            Text("Register")
        }

        Button(
            onClick = { viewModel.login(username) },
            enabled = authState !is AuthState.Loading
        ) {
            Text("Login")
        }

        // Обработка состояний
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator() // спиннер загрузки, как вращающийся значок
            is AuthState.RegistrationSuccess -> {
                // Unit — это аналог void в Kotlin, но как объект
                LaunchedEffect(Unit) {
                    navigationController.navigateToProfile()
                }
            }
            is AuthState.LoginSuccess -> {
                LaunchedEffect(Unit) {
//                    navigationController.navigateToHome()
                    navigationController.navigateToProfile()
                }
            }
            is AuthState.Error -> {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = Color.Red
                )
            }
            else -> {}
        }
    }
}