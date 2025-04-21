package com.distributed_messenger.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.distributed_messenger.ui.NavigationController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.distributed_messenger.presenter.viewmodels.AppSettingsViewModel
import com.distributed_messenger.ui.components.SettingItem

@Composable
fun AppSettingsScreen(
    viewModel: AppSettingsViewModel,
    navigationController: NavigationController
) {
    val settings by viewModel.settingsState.collectAsState()

    LazyColumn {
        items(settings) { (type, value) ->
            SettingItem(
                type = type,
                currentValue = value,
                onValueChange = { viewModel.updateSetting(type, it) }
            )
        }
    }
}