// ProfileScreen.kt
package com.distributed_messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.distributed_messenger.ui.R
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.presenter.viewmodels.ProfileViewModel
import com.distributed_messenger.presenter.viewmodels.SessionManager
import com.distributed_messenger.ui.NavigationController

@Composable
fun ProfileScreen(viewModel: ProfileViewModel,
                  navigationController: NavigationController) {
    val colorScheme = MaterialTheme.colorScheme
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()
    // Состояние для текстового поля имени
    var username by remember { mutableStateOf(SessionManager.currentUserName) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Обновляем локальное состояние при изменении данных пользователя
    LaunchedEffect(viewModel.user.collectAsState().value?.username) {
        viewModel.user.value?.username?.let {
            username = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary) // Цвет фона всего экрана
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Шапка профиля
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.20f)
                    .background(colorScheme.primary),
//                    .padding(start = 10.dp),
                //            horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .clickable { navigationController.navigateToChatList() }
//                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(25.dp),
                        tint = colorScheme.onPrimary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.20f)
                    .background(colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(17.dp))
                Icon(
                    painter = painterResource(R.drawable.avatar_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp),
                    tint = colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = username,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Поле для изменения имени
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("New Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.updateUsername(username)
                        SessionManager.updateUserName(username)
                        focusManager.clearFocus()
                        Logger.log("ProfileScreen", "Username updated to: $username")
                    }
                )
            )

            Button(
                onClick = {
                    viewModel.updateUsername(username)
                    SessionManager.updateUserName(username)
                    navigationController.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Save")
            }

            when (state) {
                is ProfileViewModel.ProfileState.Loading -> {
                    CircularProgressIndicator()
                }

                is ProfileViewModel.ProfileState.Error -> {
                    val error = (state as ProfileViewModel.ProfileState.Error)
                    Text(
                        text = error.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }
        }
    }
}

//        // Кнопка админ-панели
//        if (SessionManager.currentUserRole == UserRole.ADMINISTRATOR) {
//            Button(
//                onClick = {
//                    Logger.log("ProfileScreen", "Navigating to admin dashboard")
//                    navigationController.navigateToAdminDashboard()
//                },
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//            ) {
//                Text("Админ-панель")
//            }
//        }