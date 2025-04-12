package com.distributed_messenger.implementation.repositories

import com.distributed_messenger.domain.models.File
import com.distributed_messenger.domain.repositories.IFileRepository
import com.distributed_messenger.data.local.dao.FileDao
import com.distributed_messenger.data.local.entities.FileEntity

class FileRepository(private val fileDao: FileDao) : IFileRepository {
    override suspend fun getFile(id: Int): File? {
        return fileDao.getFileById(id)?.toDomain()
    }

    override suspend fun getAllFiles(): List<File> {
        return fileDao.getAllFiles().map { it.toDomain() }
    }

    override suspend fun getFilesByUser(userId: Int): List<File> {
        return fileDao.getFilesByUserId(userId).map { it.toDomain() }
    }

    override suspend fun addFile(file: File): Int {
        return fileDao.insertFile(file.toEntity()).toInt()
    }

    override suspend fun updateFile(id: Int, file: File): Boolean {
        return fileDao.updateFile(file.toEntity()) > 0
    }

    override suspend fun deleteFile(id: Int): Boolean {
        return fileDao.deleteFile(id) > 0
    }

    private fun File.toEntity(): FileEntity {
        return FileEntity(
            id = id,
            name = name,
            type = type,
            path = path,
            uploadedBy = uploadedBy,
            uploadedTimestamp = uploadedTimestamp
        )
    }

    private fun FileEntity.toDomain(): File {
        return File(
            id = id,
            name = name,
            type = type,
            path = path,
            uploadedBy = uploadedBy,
            uploadedTimestamp = uploadedTimestamp
        )
    }
}