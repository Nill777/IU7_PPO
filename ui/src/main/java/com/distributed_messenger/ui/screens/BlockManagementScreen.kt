package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.logging.LogLevel
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.components.BlockItem

@Composable
fun BlockManagementScreen(viewModel: AdminViewModel,
                          navigationController: NavigationController) {
    val users by viewModel.users.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Logger.log("BlockManagement", "Loading users for blocking")
        viewModel.loadUsers()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (state) {
            AdminViewModel.AdminState.Loading -> {
                Logger.log("BlockManagement", "Loading state")
                CircularProgressIndicator()
            }
            is AdminViewModel.AdminState.Error -> {
                Logger.log("BlockManagement", "Error: ${(state as AdminViewModel.AdminState.Error).message}", LogLevel.ERROR)
                Text(
                    (state as AdminViewModel.AdminState.Error).message,
                    color = Color.Red
                )
            }
            else -> {}
        }

        LazyColumn {
            items(users) { user ->
                BlockItem(
                    user = user,
                    onBlock = {
                        Logger.log("BlockManagement", "Blocking user: ${user.id}")
                        viewModel.blockUser(user.id)
                    },
                    onUnblock = {
                        Logger.log("BlockManagement", "Unblocking user: ${user.id}")
                        viewModel.unblockUser(user.id)
                    }
                )
            }
        }
    }
}