package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "profile_settings")
data class ProfileSettingsEntity(
    @PrimaryKey @ColumnInfo(name = "setting_id") val settingId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "setting_name") val settingName: String,
    @ColumnInfo(name = "setting_value") val settingValue: Int
)