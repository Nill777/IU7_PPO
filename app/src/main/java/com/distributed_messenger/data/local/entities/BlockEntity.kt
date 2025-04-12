package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

@Entity(tableName = "blocked_users")
data class BlockEntity(
    @PrimaryKey @ColumnInfo(name = "block_id") val blockId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "blocker_id") val blockerId: UUID,
    @ColumnInfo(name = "blocked_user_id") val blockedUserId: UUID,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "block_timestamp") val blockTimestamp: Instant
)