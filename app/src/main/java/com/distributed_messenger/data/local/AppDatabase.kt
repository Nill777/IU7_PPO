package com.distributed_messenger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.distributed_messenger.data.local.dao.*
import com.distributed_messenger.data.local.entities.*

@Database(
    entities = [
        MessageEntity::class,
        ChatEntity::class,
        UserEntity::class,
        FileEntity::class,
        BlockEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
    abstract fun userDao(): UserDao
    abstract fun fileDao(): FileDao
    abstract fun blockDao(): BlockDao
}