package com.distributed_messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.distributed_messenger.core.Message
import com.distributed_messenger.presenter.viewmodels.ChatViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.R

@Composable
fun MessageMenu(
    message: Message,
    position: IntOffset,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
    navigationController: NavigationController
) {
    val density = LocalDensity.current
    val menuWidth = with(density) { 200.dp.roundToPx() }
    val xOffset = (position.x - menuWidth).coerceAtLeast(0)

    // Создаёт всплывающее окно
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
//        offset = IntOffset(xOffset, position.y)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Затемнение фона
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismiss)
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // Контент меню
            Card(
                modifier = Modifier
                    .width(200.dp)
//                    .offset {
//                        IntOffset(
//                            x = xOffset,
//                            y = position.y.coerceAtLeast(0)
//                        )
//                    }
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    MessageMenuItem(
                        icon = R.drawable.message_menu_reply,
                        text = "Reply",
                        onClick = {
                            // viewModel.replyToMessage(message)
                            onDismiss()
                        }
                    )
                    MessageMenuItem(
                        icon = R.drawable.message_menu_copy,
                        text = "Copy",
                        onClick = {
                            // viewModel.copyMessage(message)
                            onDismiss()
                        }
                    )
                    MessageMenuItem(
                        icon = R.drawable.message_menu_forward,
                        text = "Forward",
                        onClick = {
                            // navigationController.navigateToForward(message.id)
                            onDismiss()
                        }
                    )
                    MessageMenuItem(
                        icon = R.drawable.message_menu_edit,
                        text = "Edit",
                        onClick = {
                            // viewModel.editMessage(message)
                            onDismiss()
                        }
                    )
                    MessageMenuItem(
                        icon = R.drawable.message_menu_history,
                        text = "History",
                        onClick = {
                            // navigationController.navigateToHistory(message.id)
                            onDismiss()
                        }
                    )
                    MessageMenuItem(
                        icon = R.drawable.message_menu_trash,
                        text = "Delete",
                        onClick = {
                            viewModel.deleteMessage(message.id)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}