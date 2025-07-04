package com.distributed_messenger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.distributed_messenger.data.local.dao.*
import com.distributed_messenger.data.local.entities.*

@Database(
    entities = [
        MessageEntity::class,
        MessageHistoryEntity::class,
        ChatEntity::class,
        UserEntity::class,
        FileEntity::class,
        BlockEntity::class,
        AppSettingsEntity::class
    ],
    version = 4
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun messageHistoryDao(): MessageHistoryDao
    abstract fun chatDao(): ChatDao
    abstract fun userDao(): UserDao
    abstract fun fileDao(): FileDao
    abstract fun blockDao(): BlockDao
    abstract fun appSettingsDao(): AppSettingsDao
}