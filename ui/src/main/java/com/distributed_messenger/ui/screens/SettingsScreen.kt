package com.distributed_messenger.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.presenter.viewmodels.SessionManager
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.R
import com.distributed_messenger.ui.components.SideMenuItem

@Composable
fun SettingsScreen(navigationController: NavigationController) {
    val colorScheme = MaterialTheme.colorScheme
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Шапка как в ProfileScreen
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenWidth * 0.20f)
                    .background(colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .clickable { navigationController.navigateBack() }
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
                    painter = painterResource(R.drawable.side_menu_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(50.dp),
                    tint = colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onPrimary
                )
            }
        }

        // Основной контент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            if (SessionManager.currentUserRole == UserRole.ADMINISTRATOR) {
                AdminSettingsPanel(navigationController)
            } else {
                AccessDeniedMessage()
            }
        }
    }
}

@Composable
private fun AdminSettingsPanel(navigationController: NavigationController) {
    // Пункты меню
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            SideMenuItem(
                icon = R.drawable.role_management,
                text = "Role Management",
                onClick = {
                    Logger.log("SettingsScreen", "Navigation: User Management")
                    navigationController.navigateToUserManagement()
                }
            )
        }
        item {
            SideMenuItem(
                icon = R.drawable.block_users,
                text = "Blocking Users",
                onClick = {
                    Logger.log("SettingsScreen", "Navigation: Block Management")
                    navigationController.navigateToBlockManagement()
                }
            )
        }
        item {
            SideMenuItem(
                icon = R.drawable.deep_application_settings,
                text = "Deep Application Settings",
                onClick = {
                    Logger.log("SettingsScreen", "Navigation: App Settings")
                    navigationController.navigateToAppSettings()
                }
            )
        }
    }
}

@Composable
private fun AccessDeniedMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Access Denied",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}