package com.distributed_messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.distributed_messenger.core.Message
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MessageItem(message: Message,
                isCurrentUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment =
            if (isCurrentUser) Alignment.CenterEnd
            else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentWidth(
                    align =
                        if (isCurrentUser) Alignment.End
                        else Alignment.Start
                    ),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (isCurrentUser) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                Text(text = message.content)
                Text(
                    text = message.formatTimestamp(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}