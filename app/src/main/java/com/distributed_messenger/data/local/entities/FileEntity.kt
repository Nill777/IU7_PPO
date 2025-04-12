package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey @ColumnInfo(name = "file_id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String? = null,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "uploaded_by") val uploadedBy: UUID,
    @ColumnInfo(name = "uploaded_timestamp") val uploadedTimestamp: Instant
)
