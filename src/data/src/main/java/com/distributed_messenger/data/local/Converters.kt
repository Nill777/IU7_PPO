package com.distributed_messenger.data.local

import androidx.room.TypeConverter
import com.distributed_messenger.core.UserRole
import java.time.Instant
import java.util.UUID

class Converters {
    // Для UUID
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? = uuidString?.let { UUID.fromString(it) }

    // Для UserRole (enum)
    @TypeConverter
    fun fromUserRole(role: UserRole?): String? = role?.name

    @TypeConverter
    fun toUserRole(roleName: String?): UserRole? =
        roleName?.let { UserRole.valueOf(it) }

    // Для Instant
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun toInstant(millis: Long?): Instant? =
        millis?.let { Instant.ofEpochMilli(it) }
}