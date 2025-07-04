package com.distributed_messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.distributed_messenger.presenter.viewmodels.ChatViewModel
import com.distributed_messenger.ui.NavigationController
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.Message
import com.distributed_messenger.presenter.viewmodels.ChatListViewModel
import com.distributed_messenger.presenter.viewmodels.SessionManager
import com.distributed_messenger.ui.R
import com.distributed_messenger.ui.components.ChatListItem
import com.distributed_messenger.ui.components.MessageItem

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.distributed_messenger.ui.components.MessageMenu
import kotlin.math.roundToInt

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@Preview
//@Composable
//fun ClipIconPreview() {
//    Icon(
//        painter = painterResource(R.drawable.clip_2),
//        contentDescription = "Test",
//        modifier = Modifier.size(100.dp)
//    )
//    IconButton(onClick = { /* Прикрепить файл */ }, modifier = Modifier
//        .background(Color.Red)
//        .clip(RectangleShape)) {
//        Icon(
//            painter = painterResource(R.drawable.clip_2),
//            modifier = Modifier.background(Color.Blue),
//            contentDescription = "Attach file"
//        )
//    }
//    Box(
//        modifier = Modifier
//            .size(48.dp) // Размер как у IconButton
//            .clickable { /* Действие */ }
//            .background(Color.Red)
//    ) {
//        Icon(
//            painter = painterResource(R.drawable.clip_2),
//            contentDescription = "Attach file",
//            modifier = Modifier.background(Color.Blue)
//        )
//    }
//}


@Composable
fun ChatScreen(viewModel: ChatViewModel,
               navigationController: NavigationController) {
    val messages by viewModel.messages.collectAsState()
    val chatInfo by viewModel.chatInfo.collectAsState()
    val editingMessage by viewModel.editingMessage.collectAsState()

    val scrollState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val snackbarHostState = remember { SnackbarHostState() }
    var lastOnline by remember { mutableStateOf("Был(а) в сети недавно") }
    var messageText by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    val deleteStatus by viewModel.deleteStatus.collectAsState()
    var contextMenuState by remember {
        // Сохраняет выбранное сообщение и его позицию на экране (в пикселях)
        mutableStateOf<Pair<Message, IntOffset>?>(null)
    }

    // Автопрокрутка к новым сообщениям
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }
    // Время в сети
    LaunchedEffect(messages) {
        val lastMessage = messages
            .filter { it.senderId != SessionManager.currentUserId }
            .maxByOrNull { it.timestamp }

        lastMessage?.let {
            val date = Date.from(it.timestamp)
            val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
            lastOnline = formatter.format(date)
        }
    }
    // Удаление чата
    LaunchedEffect(deleteStatus) {
        when (deleteStatus) {
            true -> {
                navigationController.navigateToChatList()
                viewModel.clearDeleteStatus()
            }
            false -> {
                snackbarHostState.showSnackbar(
                    message = "Failed to delete chat",
                    duration = SnackbarDuration.Short)
                viewModel.clearDeleteStatus()
            }
            null -> {} // Инициализация или сброс
        }
    }
    // Автоподстановка текста при редактировании
    LaunchedEffect(editingMessage) {
        editingMessage?.let {
            messageText = it.content
            keyboardController?.show()
        }
    }

    // Контекстное меню
    contextMenuState?.let { (message, position) ->
        MessageMenu(
            message = message,
            position = position,
            onDismiss = { contextMenuState = null },
            modifier = Modifier,
            viewModel = viewModel,
            navigationController = navigationController
        )
    }

    // Обработка отправки
    fun handleSend() {
        editingMessage?.let { msg ->
            viewModel.editMessage(msg.id, messageText)
            viewModel.cancelEditing()
        } ?: run {
            viewModel.sendMessage(messageText)
        }
        messageText = ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary) // Цвет фона всего экрана
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(colorScheme.secondary),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.15f)
                    .background(colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                    .clickable {
                        //                    listViewModel.refreshChats()
                        navigationController.navigateToChatList()
                    }
//                    .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(25.dp),
                        tint = colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
//                    modifier = Modifier
//                        .clickable { navigationController.navigateToProfile() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.avatar_placeholder),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(50.dp),
                        tint = colorScheme.onPrimary
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = chatInfo?.name ?: "Shadow",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onPrimary
                    )
                    Text(
                        text = lastOnline,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                    .clickable { isMenuExpanded = true }
                    .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vertical_menu),
                        contentDescription = "vertical_menu",
                        modifier = Modifier.size(20.dp),
                        tint = colorScheme.onPrimary
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete chat") },
                            onClick = {
                                viewModel.deleteChat()
                                isMenuExpanded = false
                                navigationController.navigateToChatList()
                            }
                        )
                    }
                }
            }

            // Список сообщений с адаптивной высотой
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .padding(horizontal = 10.dp)
            ) {
                items(messages) { message ->
                    MessageItem(
                        message = message,
                        isCurrentUser = message.senderId == SessionManager.currentUserId,
                        // При длинном нажатии на сообщение вызывается лямбда,
                        // которая сохраняет сообщение и его позицию в contextMenuState
                        onLongClick = { offset ->
                            contextMenuState = message to offset
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            //        Spacer(modifier = Modifier.height(500.dp))

            // Поле ввода
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .heightIn(min = 56.dp, max = 120.dp)
                        .clip(RectangleShape),
                    placeholder = {
                        Text(
                            text =
                                if (editingMessage != null) "Edit message"
                                else "Message",
                            color = colorScheme.onSurfaceVariant
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.background,
                        unfocusedContainerColor = colorScheme.background,
                        focusedIndicatorColor = colorScheme.background,
                        unfocusedIndicatorColor = colorScheme.background,
                        disabledIndicatorColor = colorScheme.background,
                    ),
                    shape = RectangleShape,
                    singleLine = false
                )

                Box(
                    modifier = Modifier
                        .clickable { /* Действие */ }
                        .background(colorScheme.background)
                        .padding(horizontal = 3.dp, vertical = 13.dp)
                    //                    .background(color = colorScheme.onPrimary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clip_2),
                        contentDescription = "Attach file",
                        modifier = Modifier.size(30.dp),
                        tint = colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .clickable {
                            handleSend()
//                            viewModel.sendMessage(messageText)
//                            messageText = ""
                            // штуки чтобы скрыть клавиатуру
//                            keyboardController?.hide()
//                            focusManager.clearFocus()
                        }
                        .background(colorScheme.background)
                        .padding(horizontal = 3.dp, vertical = 13.dp)
                    //                    .background(color = colorScheme.onPrimary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.quill_3),
                        contentDescription = "Send",
                        modifier = Modifier.size(30.dp),
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}
