package com.distributed_messenger.test.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.FileDao
import com.distributed_messenger.data.local.entities.FileEntity
import com.distributed_messenger.implementation.repositories.FileRepository
import com.distributed_messenger.domain.models.File
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FileRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var fileDao: FileDao
    private lateinit var repository: FileRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        fileDao = db.fileDao()
        repository = FileRepository(fileDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `getFilesByUser should return user files`() = runTest {
        // Arrange
        val files = listOf(
            FileEntity(0, "file1.txt", "text", "/path", 1, "123"),
            FileEntity(0, "file2.txt", "text", "/path", 1, "124")
        )
        files.forEach { fileDao.insertFile(it) }

        // Act
        val result = repository.getFilesByUser(1)

        // Assert
        assertEquals(2, result.size)
    }

    @Test
    fun `deleteFile should remove file`() = runTest {
        // Arrange
        val id = fileDao.insertFile(FileEntity(0, "test.txt", "text", "/path", 1, "123")).toInt()

        // Act
        val deleted = repository.deleteFile(id)
        val result = repository.getFile(id)

        // Assert
        assertTrue(deleted)
        assertNull(result)
    }
}