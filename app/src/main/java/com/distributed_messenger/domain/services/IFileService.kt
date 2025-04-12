package com.distributed_messenger.domain.services

import com.distributed_messenger.domain.models.File
import java.util.UUID

interface IFileService {
    suspend fun uploadFile(name: String, type: String, path: String, uploadedBy: UUID): UUID
    suspend fun getFile(id: UUID): File?
    suspend fun getUserFiles(userId: UUID): List<File>
    suspend fun deleteFile(id: UUID): Boolean
}
