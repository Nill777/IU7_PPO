package com.distributed_messenger.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.ui.NavigationController

@Composable
fun AdminDashboardScreen(viewModel: AdminViewModel,
                         navigationController: NavigationController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { navigationController.navigateToUserManagement() },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text(
                text = "Управление ролями",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Button(
            onClick = { navigationController.navigateToBlockManagement() },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text(
                text = "Блокировка пользователей",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Button(
            onClick = { navigationController.navigateToAppSettings() },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text(
                text = "Настройки приложения",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
    }
}