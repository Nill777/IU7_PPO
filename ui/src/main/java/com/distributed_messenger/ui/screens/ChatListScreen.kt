package com.distributed_messenger.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.distributed_messenger.presenter.viewmodels.ChatListViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.components.ChatListItem
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributed_messenger.core.AppSettingType
import com.distributed_messenger.core.Chat
import com.distributed_messenger.core.Message
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.presenter.viewmodels.AppSettingsViewModel
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.presenter.viewmodels.SessionManager
import com.distributed_messenger.ui.components.SideMenuItem
import com.distributed_messenger.ui.R
import java.util.UUID

//@Preview
//@Composable
//fun ClipIconPreview() {
//    Icon(
//        painter = painterResource(R.drawable.sun_theme),
//        tint = Color.Unspecified,
//        contentDescription = "Test icon"
//    )
//}

@Composable
fun ChatListScreen(viewModel: ChatListViewModel,
                   authViewModel: AuthViewModel,
                   appSettingsViewModel: AppSettingsViewModel,
                   navigationController: NavigationController) {
    val chats by viewModel.chats.collectAsState()
    val lastMessages by viewModel.lastMessages.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.9f
    var isMenuOpen by remember { mutableStateOf(false) }
    val currentTheme by appSettingsViewModel.themeState.collectAsState()

    // при каждом открытии списка чатов обновляем, чтобы подтянуть изменения
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Анимации для панели и фона
    val menuOffset by animateDpAsState(
        targetValue = if (isMenuOpen) 0.dp else -drawerWidth,
        label = "menu_animation"
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isMenuOpen) 0.4f else 0f,
        label = "background_alpha"
    )


    // Основной контент
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isMenuOpen) 0.7f else 1f)
        ) {
            if (chats.isEmpty()) {
                EmptyChatList(navigationController)
            } else {
                ChatListContent(
                    viewModel = viewModel,
                    chats = chats,
                    lastMessages = lastMessages,
                    navigationController = navigationController,
                    onMenuClick = { isMenuOpen = true }
                )
            }
        }

        // Затемненный фон с кликабельной областью
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isMenuOpen = false }
                    .background(Color.Black.copy(alpha = backgroundAlpha))
            )
        }

        // Боковая панель меню
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .offset(x = menuOffset)
                .shadow(16.dp)
                .background(colorScheme.primary)
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(colorScheme.background)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        if (dragAmount < 0 && isMenuOpen) {
                            isMenuOpen = false
                        }
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Шапка меню
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth * 0.40f)
                        .background(colorScheme.primary),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Левая колонка с информацией пользователя
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.avatar_placeholder),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(64.dp),
                            tint = colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = SessionManager.currentUserName,
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onPrimary
                        )

                        Text(
                            text = "${SessionManager.currentUserId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onPrimary.copy(alpha = 0.7f),
                        )
                    }

                    // Правая колонка с кнопкой темы
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(
                            onClick = {
                                val newTheme = when (currentTheme) {
                                    0 -> 1 // Системная -> Светлая
                                    1 -> 2 // Светлая -> Тёмная
                                    else -> 0 // Тёмная -> Системная
                                }
                                appSettingsViewModel.updateSetting(
                                    AppSettingType.THEME,
                                    newTheme
                                )
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    when (currentTheme) {
                                        1 -> R.drawable.sun_theme
                                        2 -> R.drawable.moon_theme_1
                                        else -> R.drawable.system_theme
                                    }
                                ),
                                contentDescription = "Theme",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                // Пункты меню
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        SideMenuItem(
                            icon = R.drawable.side_menu_profile,
                            text = "My Profile",
                            onClick = { navigationController.navigateToProfile() }
                        )
                    }
                    item {
                        SideMenuItem(
                            icon = R.drawable.side_menu_logout,
                            text = "Logout",
                            onClick = {
                                authViewModel.logout()
                                navigationController.navigateToAuth()
                            }
                        )
                    }
                    item {
                        SideMenuItem(
                            icon = R.drawable.side_menu_settings,
                            text = "Settings",
                            onClick = { navigationController.navigateToSettings() }
                        )
                    }
                    item {
                        SideMenuItem(
                            icon = R.drawable.side_menu_qrcode,
                            text = "About the Program",
                            onClick = { navigationController.navigateToAboutProgram() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChatList(navigationController: NavigationController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { navigationController.navigateToNewChat() }) {
                Text("Create new chat")
            }
        }
    }
}

@Composable
private fun ChatListContent(
    viewModel: ChatListViewModel,
    chats: List<Chat>,
    lastMessages: Map<UUID, Message?>,
    navigationController: NavigationController,
    onMenuClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.primary)
                .statusBarsPadding()
//                .imePadding()
                .navigationBarsPadding()
                .background(colorScheme.secondary)
        ) {
            // Хедер с кнопкой меню
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.15f)
                    .background(colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu_1),
                    contentDescription = "Menu",
                    modifier = Modifier
                        //                    .background(Color.Blue)
                        .clickable { onMenuClick() }
                        .padding(10.dp)
                        .size(38.dp),
                    tint = colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "DiMs",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onPrimary
                )
            }

            // Список чатов
            LazyColumn {
                items(chats) { chat ->
                    ChatListItem(
                        chatName = viewModel.displayNames.value[chat.id] ?: "Shadow chat",
                        lastMessage = lastMessages[chat.id],
                        onClick = { navigationController.navigateToChat(chat.id) }
                    )
                }
            }
        }

        // Кнопка создания нового чата
        FloatingActionButton(
            onClick = { navigationController.navigateToNewChat() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(R.drawable.new_chat),
                contentDescription = "new_chat",
                modifier = Modifier.size(50.dp),
                tint = colorScheme.onPrimary
            )
        }
    }
}
