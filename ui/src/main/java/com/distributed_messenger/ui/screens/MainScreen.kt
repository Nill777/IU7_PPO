package com.distributed_messenger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.ui.NavigationController
import java.util.UUID

@Composable
fun MainScreen(navigationController: NavigationController) {
    LaunchedEffect(Unit) {
        Logger.log("MainScreen", "Screen initialized")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Главный экран",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Добро пожаловать в приложение!")
    }
}