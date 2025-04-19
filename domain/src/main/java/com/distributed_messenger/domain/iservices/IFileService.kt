package com.distributed_messenger.domain.iservices

import com.distributed_messenger.core.File
import java.util.UUID

interface IFileService {
    suspend fun uploadFile(name: String, type: String, path: String, uploadedBy: UUID): UUID
    suspend fun getFile(id: UUID): File?
    suspend fun getUserFiles(userId: UUID): List<File>
    suspend fun deleteFile(id: UUID): Boolean
}
