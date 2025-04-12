package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import com.distributed_messenger.data.local.entities.UserEntity
import java.util.UUID

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE user_id = :id")
    suspend fun getUserById(id: UUID): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity): Int

    // @Delete
    @Query("DELETE FROM users WHERE user_id = :id")
    suspend fun deleteUser(id: UUID): Int
}