package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import com.distributed_messenger.data.local.entities.BlockEntity
import java.util.UUID

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocked_users WHERE block_id = :id")
    suspend fun getBlockById(id: UUID): BlockEntity?

    @Query("SELECT * FROM blocked_users")
    suspend fun getAllBlocks(): List<BlockEntity>

    @Query("SELECT * FROM blocked_users WHERE blocker_id = :userId OR blocked_user_id = :userId")
    suspend fun getBlocksByUserId(userId: UUID): List<BlockEntity>

    @Insert
    suspend fun insertBlock(block: BlockEntity): Long

    @Update
    suspend fun updateBlock(block: BlockEntity): Int

    // @Delete
    @Query("DELETE FROM blocked_users WHERE block_id = :id")
    suspend fun deleteBlock(id: UUID): Int
}
