package com.distributed_messenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.presenter.viewmodels.NewChatViewModel
import com.distributed_messenger.ui.NavigationController

@Composable
fun NewChatScreen(viewModel: NewChatViewModel,
                  navigationController: NavigationController) {
    var username by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            when (state) {
                is NewChatViewModel.NewChatState.Loading -> {
                    CircularProgressIndicator()
                }

                is NewChatViewModel.NewChatState.Error -> {
                    Text(
                        text = (state as NewChatViewModel.NewChatState.Error).message,
                        color = colorScheme.error
                    )
                }

                else -> {}
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .background(colorScheme.background)
            )

            Button(
                onClick = {
                    viewModel.createChat(username) { chatId ->
                        navigationController.navigateToChat(chatId)
                    }
                },
//                enabled = username.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create chat",
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}