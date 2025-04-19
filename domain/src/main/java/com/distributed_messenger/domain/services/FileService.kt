package com.distributed_messenger.domain.services

import com.distributed_messenger.core.File
import com.distributed_messenger.domain.iservices.IFileService
import com.distributed_messenger.domain.irepositories.IFileRepository
import java.util.UUID
import java.time.Instant

class FileService(private val fileRepository: IFileRepository) : IFileService {
    override suspend fun uploadFile(name: String, type: String, path: String, uploadedBy: UUID): UUID {
        val file = File(
            id = UUID.randomUUID(),
            name = name,
            type = type,
            path = path,
            uploadedBy = uploadedBy,
            uploadedTimestamp = Instant.now()
        )
        return fileRepository.addFile(file)
    }

    override suspend fun getFile(id: UUID): File? {
        return fileRepository.getFile(id)
    }

    override suspend fun getUserFiles(userId: UUID): List<File> {
        return fileRepository.getFilesByUser(userId)
    }

    override suspend fun deleteFile(id: UUID): Boolean {
        return fileRepository.deleteFile(id)
    }
}
