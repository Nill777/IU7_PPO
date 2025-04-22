package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.ui.NavigationController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.distributed_messenger.ui.components.RoleItem

@Composable
fun AdminPanelScreen(viewModel: AdminViewModel,
                     navigationController: NavigationController) {
    val users by viewModel.users.collectAsState()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        when (state) {
            AdminViewModel.AdminState.Loading -> CircularProgressIndicator()
            is AdminViewModel.AdminState.Error -> Text(
                (state as AdminViewModel.AdminState.Error).message,
                color = Color.Red
            )
            else -> {}
        }

        LazyColumn {
            items(users) { user ->
                RoleItem(
                    user = user,
                    onRoleChange = { newRole ->
                        viewModel.updateUserRole(user.id, newRole)
                    }
                )
            }
        }
    }
}