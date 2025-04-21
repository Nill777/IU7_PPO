package com.distributed_messenger.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole

@Composable
fun UserListItem(
    user: User,
    onRoleChange: (UserRole) -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(user.username)

//        Button(onClick = onBlock) {
//            Text("Заблокировать")
//        }
//
//        Button(onClick = onUnblock) {
//            Text("Разблокировать")
//        }

        Box {
            Button(onClick = { expanded = true }) {
                Text(user.role.name)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                UserRole.entries.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
                        onClick = {
                            onRoleChange(role)
                            expanded = false
                        }
                    )
                }
            }
        }
        IconButton(onClick = {
            if (user.blockedUsersId != null) {
                onUnblock() // Если пользователь заблокирован, разблокируем
            } else {
                onBlock() // Если пользователь не заблокирован, блокируем
            }
        })
        {
            Icon(
                imageVector = if (user.blockedUsersId != null) Icons.Filled.Lock else Icons.Filled.Done,
                contentDescription = if (user.blockedUsersId != null) "Unblock" else "Block",
                tint = if (user.blockedUsersId != null) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        }
    }
}