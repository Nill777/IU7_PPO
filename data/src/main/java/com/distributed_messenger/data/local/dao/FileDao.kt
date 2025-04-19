package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import com.distributed_messenger.data.local.entities.FileEntity
import java.util.UUID

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE file_id = :id")
    suspend fun getFileById(id: UUID): FileEntity?

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("SELECT * FROM files WHERE uploaded_by = :userId")
    suspend fun getFilesByUserId(userId: UUID): List<FileEntity>

    @Insert
    suspend fun insertFile(file: FileEntity): Long

    @Update
    suspend fun updateFile(file: FileEntity): Int

    // @Delete
    @Query("DELETE FROM files WHERE file_id = :id")
    suspend fun deleteFile(id: UUID): Int
}
