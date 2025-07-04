package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "app_settings",
    indices = [Index(value = ["setting_name"], unique = true)]
)
data class AppSettingsEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "setting_id") val id: Int = 0,
    @ColumnInfo(name = "setting_name") val name: String,
    @ColumnInfo(name = "setting_value") val value: Int
)