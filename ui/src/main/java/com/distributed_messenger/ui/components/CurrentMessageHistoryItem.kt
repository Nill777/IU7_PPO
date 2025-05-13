package com.distributed_messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.Message
import com.distributed_messenger.ui.R

@Composable
fun CurrentMessageItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.history_current_1),
            contentDescription = "Current version",
            tint = colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = message.content,
                style = typography.bodyMedium,
                color = colorScheme.onBackground
            )
            Text(
                text = "Current version",
                style = typography.labelSmall,
                color = colorScheme.outline
            )
        }
    }
}