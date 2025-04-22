package com.distributed_messenger.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.AppSettingType

@Composable
fun SettingItem(type: AppSettingType,
                currentValue: Int,
                onValueChange: (Int) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название настройки
        Text(
            text = type.settingName,
            modifier = Modifier.weight(1f)
        )
                    // Кнопка с текущим значением
        Button(onClick = { showMenu = true }) {
            Text(type.possibleValues[currentValue] ?: "")
        }

                    // Выпадающее меню
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            type.possibleValues.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onValueChange(key)
                        showMenu = false
                    }
                )
            }
        }
    }
}