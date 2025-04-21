package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.File
import com.distributed_messenger.domain.irepositories.IFileRepository
import com.distributed_messenger.data.local.dao.FileDao
import com.distributed_messenger.data.local.entities.FileEntity
import java.util.UUID

class FileRepository(private val fileDao: FileDao) : IFileRepository {
    override suspend fun getFile(id: UUID): File? {
        return fileDao.getFileById(id)?.toDomain()
    }

    override suspend fun getAllFiles(): List<File> {
        return fileDao.getAllFiles().map { it.toDomain() }
    }

    override suspend fun getFilesByUser(userId: UUID): List<File> {
        return fileDao.getFilesByUserId(userId).map { it.toDomain() }
    }

    override suspend fun addFile(file: File): UUID {
        val rowId = fileDao.insertFile(file.toEntity())
        if (rowId == -1L) {
            throw Exception("Failed to insert file")
        }
        return file.id
    }

    override suspend fun updateFile(file: File): Boolean {
        return fileDao.updateFile(file.toEntity()) > 0
    }

    override suspend fun deleteFile(id: UUID): Boolean {
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