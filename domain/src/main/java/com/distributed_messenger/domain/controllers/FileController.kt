package com.distributed_messenger.domain.controllers

import com.distributed_messenger.domain.iservices.IFileService
import java.util.UUID

class FileController(private val fileService: IFileService) {
    suspend fun uploadFile(
        name: String,
        type: String,
        path: String,
        uploadedBy: UUID
    ) = fileService.uploadFile(name, type, path, uploadedBy)

    suspend fun getFile(id: UUID) = fileService.getFile(id)

    suspend fun getUserFiles(userId: UUID) =
        fileService.getUserFiles(userId)

    suspend fun deleteFile(id: UUID) =
        fileService.deleteFile(id)
}