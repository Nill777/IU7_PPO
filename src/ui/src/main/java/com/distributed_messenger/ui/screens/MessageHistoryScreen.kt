package com.distributed_messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.Message
import com.distributed_messenger.core.MessageHistory
import com.distributed_messenger.presenter.viewmodels.MessageHistoryViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.R
import com.distributed_messenger.ui.components.CurrentMessageItem
import com.distributed_messenger.ui.components.HistoryItem
import java.util.UUID

@Composable
fun MessageHistoryScreen(
    viewModel: MessageHistoryViewModel,
    messageId: UUID,
    navigationController: NavigationController
) {
    val history by viewModel.history.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val entries = remember(currentMessage, history) {
        listOfNotNull(currentMessage) + history
    }

    LaunchedEffect(messageId) {
        viewModel.loadHistory(messageId)
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
                            navigationController.navigateBack()
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
                        painter = painterResource(R.drawable.message_menu_history),
                        contentDescription = "History",
                        modifier = Modifier.size(40.dp),
                        tint = colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = "Message history",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onPrimary
                )
            }

            LazyColumn(
                modifier = Modifier
//                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
            ) {
                items(entries) { entry ->
                    if (entry is Message) {
                        CurrentMessageItem(message = entry)
                    } else {
                        HistoryItem(entry = entry as MessageHistory)
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
        }
    }
}
