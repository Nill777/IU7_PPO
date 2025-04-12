package com.distributed_messenger.domain.repositories

import com.distributed_messenger.domain.models.File
import java.util.UUID

interface IFileRepository {
    suspend fun getFile(id: UUID): File?
    suspend fun getAllFiles(): List<File>
    suspend fun getFilesByUser(userId: UUID): List<File>
    suspend fun addFile(file: File): UUID
    suspend fun updateFile(file: File): Boolean
    suspend fun deleteFile(id: UUID): Boolean
}
